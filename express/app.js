const express    = require('express');
const admin      = require('firebase-admin');
const https   = require('https');
const fs      = require('fs');
const fetch = require('node-fetch');   // npm i node-fetch@2
const bodyParser = require('body-parser');
const cors       = require('cors');
const serviceAccount = require('./serviceAccountKey.json');

const FIREBASE_API_KEY = process.env.FIREBASE_API_KEY;
if (!FIREBASE_API_KEY) {
  console.error('⚠️ Please set FIREBASE_API_KEY in your env');
  process.exit(1);
}

// Initialize Firebase Admin
admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
  databaseURL: `https://${serviceAccount.project_id}.firebaseio.com`
});

const db = admin.firestore();
const app = express();
app.use(cors());
app.use(bodyParser.json());


/**
 * POST /register
 * Body: { email, password, username }
 * → Creates Auth user + a Firestore “users/{uid}” doc
 */
app.post('/register', async (req, res) => {
  const { email, password, username } = req.body;
  // Basic validation
  if (!email || !password || password.length < 6 || !username) {
    return res.status(400).json({ error: 'Missing or invalid fields' });
  }

  try {
    // 1) Create Auth user
    const userRecord = await admin.auth().createUser({
      email,
      password,
      displayName: username
    });

    // 2) Create Firestore profile
    await db.collection('users')
            .doc(userRecord.uid)
            .set({ username, createdAt: admin.firestore.FieldValue.serverTimestamp() });

    // 3) Respond with minimal info
    res.json({
      uid: userRecord.uid,
      email: userRecord.email,
      username: userRecord.displayName
    });
  } catch (error) {
    res.status(400).json({ error: error.message });
  }
});

/**
 * POST /login
 * Body: { email, password }
 * → Verifies via Firebase Auth REST API, returns idToken
 */
app.post('/login', async (req, res) => {
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
    // data.idToken, data.refreshToken, data.localId (uid), expiresIn
    res.json({
      idToken:    data.idToken,
      refreshToken: data.refreshToken,
      uid:        data.localId,
      expiresIn:  data.expiresIn
    });
  } catch (error) {
    console.error("ERROR U MORON :", error);
    res.status(500).json({ error: error.message });
  }
});

/**
 * Middleware: checks for `Authorization: Bearer <idToken>`
 * and populates req.uid
 */
async function authenticate(req, res, next) {
  const auth = req.headers.authorization;
  if (!auth?.startsWith('Bearer ')) {
    return res.status(401).json({ error: 'Missing Bearer token' });
  }
  const idToken = auth.split(' ')[1];
  try {
    const decoded = await admin.auth().verifyIdToken(idToken);
    req.uid = decoded.uid;const options = {
      key:  fs.readFileSync(__dirname + '/ssl/key.pem'),
      cert: fs.readFileSync(__dirname + '/ssl/cert.pem')
    };
    next();
  } catch (err) {
    res.status(401).json({ error: 'Invalid or expired token' });
  }
}

/**
 * GET /users/:uid
 * → Returns the Firestore profile, or 404 if none
 */
app.get('/users/:uid', authenticate, async (req, res) => {
  if (req.uid !== req.params.uid) {
    return res.status(403).json({ error: 'Forbidden' });
  }
  try {
    const snap = await db.collection('users').doc(req.uid).get();
    if (!snap.exists) {
      return res.status(404).json({ error: 'Profile not found' });
    }
    res.json(snap.data());
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

/**
 * PUT /users/:uid
 * Body: { username, ...any other fields }
 * → Updates (or creates) the profile document
 */
app.put('/users/:uid', authenticate, async (req, res) => {
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
const options = {
  key:  fs.readFileSync(__dirname + '/ssl/key.pem'),
  cert: fs.readFileSync(__dirname + '/ssl/cert.pem')
};
// Creating a TLSS server
https.createServer(options, app)
     .listen(3443, () =>
       console.log('HTTPS Express listening on https://localhost:3443')
     );