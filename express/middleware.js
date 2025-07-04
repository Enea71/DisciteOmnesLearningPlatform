const admin = require('firebase-admin');
const db = admin.firestore();
async function authenticate(req, res, next) {
  const auth = req.headers.authorization || '';
  if (!auth.startsWith('Bearer ')) {
    return res.status(401).json({ error: 'Missing Bearer token' });
  }
  const idToken = auth.split(' ')[1];
  try {
    const decoded = await admin.auth().verifyIdToken(idToken);
    req.uid   = decoded.uid;
    req.email = decoded.email;
    next();
  } catch (err) {
    console.error('Auth error', err);
    res.status(401).json({ error: 'Invalid or expired token' });
  }
}
async function checkUsername(req, res, next) {
  try {
    const newUsername = req.body.username;
    if (!newUsername) {
      return res.status(400).json({ error: 'No username provided' });
    }

    // Query for any user with that username
    const usersRef = db.collection('users');
    const snapshot = await usersRef
      .where('username', '==', newUsername)
      .get();

    // If any matching doc is _not_ the current user, it’s taken
    const conflict = snapshot.docs.some(doc => doc.id !== req.uid);

    if (conflict) {
      return res.status(409).json({ error: 'Username already exists' });
    }

    // No conflict → allow the request to proceed
    next();

  } catch (err) {
    console.error('checkUsername error', err);
    res.status(500).json({ error: 'Internal server error' });
  }
}

module.exports = { authenticate,checkUsername };
