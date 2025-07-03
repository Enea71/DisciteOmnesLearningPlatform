const express = require('express');
const router  = express.Router();
const admin   = require('firebase-admin');
const db      = admin.firestore();
const { authenticate } = require('./middleware');

// GET /groups?filterField=…&filterValue=…
router.get('/', authenticate, async (req, res) => {
  try {
    let query = db.collection('groups');
    if (req.query.filterField && req.query.filterValue) {
      query = query.where(req.query.filterField, '==', req.query.filterValue);
    }
    const snap = await query.get();
    const groups = snap.docs.map(d => ({ id: d.id, ...d.data() }));
    res.json(groups);
  } catch (err) {
    console.error('GET /groups', err);
    res.status(500).json({ error: err.message });
  }
});

// POST /groups
router.post('/', authenticate, async (req, res) => {
  try {
    const data = {
      ...req.body,
      createdBy:  req.uid,
      createdAt: admin.firestore.FieldValue.serverTimestamp()
    };
    const ref = await db.collection('groups').add(data);
    res.status(201).json({ id: ref.id, ...data });
  } catch (err) {
    console.error('POST /groups', err);
    res.status(500).json({ error: err.message });
  }
});


// POST /groups
// Body: { name, description, members: [uid1, uid2, …], … }
router.post('/', authenticate, async (req, res) => {
  try {
    const data = {
      ...req.body,
      createdBy: req.uid,
      createdAt: admin.firestore.FieldValue.serverTimestamp()
    };
    const ref = await db.collection('groups').add(data);
    res.status(201).json({ id: ref.id, ...data });
  } catch (err) {
    console.error('POST /groups failed:', err);
    res.status(500).json({ error: err.message });
  }
});

// GET /groups/:gid
router.get('/:gid', authenticate, async (req, res) => {
  try {
    const doc = await db.collection('groups').doc(req.params.gid).get();
    if (!doc.exists) {
      return res.status(404).json({ error: 'Group not found' });
    }
    res.json({ id: doc.id, ...doc.data() });
  } catch (err) {
    console.error('GET /groups/:gid failed:', err);
    res.status(500).json({ error: err.message });
  }
});

// PUT /groups/:gid
// Body: { name?, description?, members?, … }
router.put('/:gid', authenticate, async (req, res) => {
  try {
    // you might want to check ownership here, e.g. doc.data().createdBy === req.uid
    await db
      .collection('groups')
      .doc(req.params.gid)
      .set(req.body, { merge: true });
    res.json({ success: true });
  } catch (err) {
    console.error('PUT /groups/:gid failed:', err);
    res.status(500).json({ error: err.message });
  }
});

// DELETE /groups/:gid
router.delete('/:gid', authenticate, async (req, res) => {
  try {
    // optional check: only allow creator to delete
    await db.collection('groups').doc(req.params.gid).delete();
    res.json({ success: true });
  } catch (err) {
    console.error('DELETE /groups/:gid failed:', err);
    res.status(500).json({ error: err.message });
  }
});

module.exports = router;