const express = require('express');
const router  = express.Router();
const admin   = require('firebase-admin');
const db      = admin.firestore();
const { authenticate, checkUsername } = require('./middleware');
const fetch             = require('node-fetch');    

const FIREBASE_API_KEY = process.env.FIREBASE_API_KEY;
if (!FIREBASE_API_KEY) {
  console.error('⚠️ Please set FIREBASE_API_KEY in your env');
  process.exit(1);
}
/**
 * POST users/register
 * Body: { email, password, username }
 * → Creates Auth user + a Firestore “users/{uid}” doc
 */
router.post('/register',checkUsername, async (req, res) => {
  const { email, password, username } = req.body;
  if (!username) {
    return res.status(400).json({ error: 'Username required' });
  }

  try {
    // Create Auth user
    const userRecord = await admin.auth().createUser({
      email,
      password,
      displayName: username
    });

    // Create Firestore profile
    await db.collection('users')
            .doc(userRecord.uid)
            .set({ username, createdAt: admin.firestore.FieldValue.serverTimestamp() });

    // Respond with user info
    res.json({
      uid: userRecord.uid,
      email: userRecord.email,
      username: userRecord.displayName,
      idToken : userRecord.idToken
    });
  } catch (error) {
    res.status(400).json({ error: error.message });
  }
});
/**
 * POST users/login
 * Body: { email, password }
 * → Verifies via Firebase Auth REST API, returns idToken
 */
router.post('/login', async (req, res) => {
  const { email, password } = req.body;
  if (!email || !password) {
    return res.status(400).json({ error: 'Email and password required' });
  }

  try {
    const resp = await fetch(
      `https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=${FIREBASE_API_KEY}`,
      {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, password, returnSecureToken: true })
      }
    );
    const data = await resp.json();
    if (data.error) {
      return res.status(401).json({ error: data.error.message });
    }

    // data.idToken, data.refreshToken, data.localId (uid), expiresIn, username
    res.json({
      idToken:      data.idToken,
      refreshToken: data.refreshToken,
      uid:          data.localId,
      expiresIn:    data.expiresIn,
      username:     data.displayName,
      email:        data.email       
    });
  } catch (error) {
    console.error("ERROR U MORON :", error);
    res.status(500).json({ error: error.message });
  }
});

/**
 * DELETE /users/:uid/removeUser
 * → Deletes both the Auth user and their Firestore profile
 */
router.delete(
  '/:uid/removeUser',
  authenticate,  // verify Bearer token and populate req.uid
  async (req, res) => {
    const { uid } = req.params;

    // Make sure the caller owns this account
    if (req.uid !== uid) {
      return res.status(403).json({ error: 'Forbidden' });
    }

    try {
      // Delete from Firebase Auth
      await admin.auth().deleteUser(uid);

      // Delete the Firestore profile document
      await db.collection('users').doc(uid).delete();

      // Respond with 204 No Content (or 200 with a body)
      return res.status(204).end();
    } catch (err) {
      console.error('Error deleting user', err);
      return res.status(500).json({ error: err.message });
    }
  }
);
/**
 * POST /users/:uid/password
 * Body: { newPassword }
 * → Updates Auth user password
 */

router.post('/:uid/password',authenticate,      
  async (req, res) => {
    if (req.uid !== req.params.uid) {
      return res.status(403).json({ error: 'Forbidden' });
    }
    const { newPassword } = req.body;
    if (typeof newPassword !== 'string' || newPassword.length < 6) {
      return res.status(400).json({ error: 'Password must be at least 6 characters' });
    }

    try {
      await admin.auth().updateUser(req.uid, { password: newPassword });
      return res.json({ success: true });
    } catch (err) {
      console.error('Password change failed', err);
      return res.status(500).json({ error: err.message });
    }
  }
);



// PUT /users/:uid/username
// Body: { username: "new username" }
router.put('/:uid/username',authenticate, checkUsername,
    async (req, res) => {
      // Ensure caller owns this account
      if (req.uid !== req.params.uid) {
        return res.status(403).json({ error: 'Forbidden' });
      }
      const { uid } = req.params;

      // Validate input
      const { username } = req.body;
      if (typeof username !== 'string' || username.trim() === '') {
        return res.status(400).json({ error: 'username is required' });
      }
      const clean = String(username || '').trim();
      if (!clean) {
        return res.status(400).json({ error: 'username is required' });
    }
    try {
      // Update Firebase Auth displayName
      await admin.auth().updateUser(uid, { displayName: clean });

      // 4) Update Firestore profile
      await db.collection('users')
              .doc(uid)
              .set({ username: clean }, { merge: true });

      return res.json({ success: true });
    } catch (err) {
      console.error('Username change failed:', err);
      return res.status(500).json({ error: err.message });
    }
  }
  );

/**
 * GET /users/getusername/:uid
 * → Returns the username
 */

router.get('/getusername/:uid', async (req, res) => {
  const { uid } = req.params;

  try {
    const snap = await db.collection('users').doc(uid).get();
    if (!snap.exists) {
      return res.status(404).json({ error: 'User not found' });
    }

    const { username } = snap.data();
    return res.json({ username });

  } catch (err) {
    console.error(`GET /users/${uid} failed:`, err);
    return res.status(500).json({ error: err.message });
  }
});

router.get('/getuid/:username', async (req, res) => {
  const { username } = req.params;
  try {
    const snap = await db.collection('users').where('username', '==', username).get();
    if (snap.empty) {
      return res.status(404).json({ error: 'User not found' });
    }
    const uid  = snap.docs[0].id;
    return res.json({uid});

  } catch (err) {
    console.error(`GET /users/${username} failed:`, err);
    return res.status(500).json({ error: err.message });
  }
});

























/**
 * PUT /users/:uid
 * Body: { username, ...any other fields }
 * → Updates (or creates) the profile document
 */
router.put('/users/:uid', authenticate, async (req, res) => {
  if (req.uid !== req.params.uid) {
    return res.status(403).json({ error: 'Forbidden' });
  }
  try {
    await db.collection('users')
            .doc(req.uid)
            .set(req.body, { merge: true });
    res.json({ success: true });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});
module.exports = router;
