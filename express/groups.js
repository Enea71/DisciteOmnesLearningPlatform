const express = require('express');
const router  = express.Router();
const admin   = require('firebase-admin');
const db      = admin.firestore();
const https   = require('https');
const fs      = require('fs');
const fetch = require('node-fetch');   // npm i node-fetch@2
const { authenticate } = require('./middleware');
/*
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
});*/
// POST /groups
// Body: { name, description, members: [uid1, uid2, …], … }
router.post('/create', authenticate, async (req, res) => {
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

// GET /groups/:id/tasks
router.get('/:id/tasks', authenticate, async (req, res) => {
  const groupId = req.params.id;
  try {
    const tasksSnap = await db
      .collection('groups')
      .doc(groupId)
      .collection('tasks')
      .orderBy('createdAt', 'asc')
      .get();

    const tasks = tasksSnap.docs.map(doc => ({
      id: doc.id,
      ...doc.data()
    }));

    res.status(200).json(tasks);
  } catch (err) {
    console.error(`GET /groups/${groupId}/tasks failed:`, err);
    res.status(500).json({ error: err.message });
  }
});

// POST /groups/:id/tasks
router.post('/:id/tasks', authenticate, async (req, res) => {
  const groupId = req.params.id;
  try {
    const data = {
      ...req.body,
      groupId,
      createdBy: req.uid,
      createdAt: admin.firestore.FieldValue.serverTimestamp()
    };
    const ref = await db
      .collection('groups')
      .doc(groupId)
      .collection('tasks')
      .add(data);

    res.status(201).json({ id: ref.id, ...data });
  } catch (err) {
    console.error(`POST /groups/${groupId}/tasks failed:`, err);
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
    await db.collection('groups').doc(req.params.gid).delete();
    res.json({ success: true });
  } catch (err) {
    console.error('DELETE /groups/:gid failed:', err);
    res.status(500).json({ error: err.message });
  }
});

// GET /groups/member → all groups where you are in members[]
router.get('/member', authenticate, async (req, res) => {
  try {
    const snap = await db
      .collection('groups')
      .where('members', 'array-contains', req.uid)
      .get();

    console.log(`GET /groups/member → user ${req.uid} is in ${snap.size} groups`);
    const groups = snap.docs.map(d => ({ id: d.id, ...d.data() }));
    res.json({ groups });

  } catch (err) {
    console.error('GET /groups/member failed:', err);
    res.status(500).json({ error: err.message });
  }
});

// GET /groups/owner → all groups you created
router.get('/owner', authenticate, async (req, res) => {
  try {
    const snap = await db
      .collection('groups')
      .where('createdBy', '==', req.uid)
      .get();
    const groups = snap.docs.map(d => ({ id: d.id, ...d.data() }));
    res.json({ groups });
  } catch (err) {
    console.error('GET /groups/owner failed:', err);
    res.status(500).json({ error: err.message });
  }
});
router.put('/:id', authenticate, async (req, res) => {
  const db   = req.app.locals.db;
  const id   = req.params.id;
  const data = req.body;

  const groupRef = db.collection('groups').doc(id);
  
  try {
    const snap = await groupRef.get();
    if (!snap.exists) {
      return res.status(404).json({ error: 'Group not found' });
    }

    const group = snap.data();
    // Ensure only owner can update:
    if (group.owner !== req.user.uid) {
      return res.status(403).json({ error: 'Not authorized to edit this group' });
    }

    // Merge the incoming fields (name, description, etc.)
    await groupRef.update({
      ...data,
      updatedAt: FieldValue.serverTimestamp()
    });

    // Return the fresh document
    const updatedSnap = await groupRef.get();
    res.json({ id: updatedSnap.id, ...updatedSnap.data() });
  } catch (err) {
    console.error('Error updating group:', err);
    res.status(500).json({ error: err.message });
  }
});

// GET /groups/:gid  — fetch a single group by ID
router.get('/:gid', authenticate, async (req, res) => {
  try {
    const doc = await db.collection('groups').doc(req.params.gid).get();
    if (!doc.exists) {
      return res.status(404).json({ error: 'Group not found' });
    }
    res.json({ id: doc.id, ...doc.data() });
  } catch (err) {
    console.error(`GET /groups/${req.params.gid} failed:`, err);
    res.status(500).json({ error: err.message });
  }
});

module.exports = router;