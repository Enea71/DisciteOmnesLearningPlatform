const express    = require('express');
const admin      = require('firebase-admin');
const https   = require('https');
const fs      = require('fs');
const fetch = require('node-fetch');   // npm i node-fetch@2
const bodyParser = require('body-parser');
const cors       = require('cors');
const serviceAccount = require('./serviceAccountKey.json');
require('dotenv').config();


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

const groupsRouter = require('./groups');
const usersRouter = require('./users')

const { authenticate } = require('./middleware');

app.use('/groups', groupsRouter);
app.use('/users', usersRouter);
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
    const profile = snap.data();

    return res.json({
      email:    req.email,        
      username: profile.username, 
    });

  } catch (err) {
    console.error(err);
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