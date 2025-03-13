// Mock resources for demo purposes
const resources = [
  { id: 1, name: 'Resource 1', description: 'This is resource 1', ownerId: 1 },
  { id: 2, name: 'Resource 2', description: 'This is resource 2', ownerId: 1 },
  { id: 3, name: 'Resource 3', description: 'This is resource 3', ownerId: 2 }
];

exports.getAllResources = (req, res) => {
  try {
    // Filter resources based on user role and ownership
    let userResources;
    
    if (req.user.role === 'admin') {
      // Admin can see all resources
      userResources = resources;
    } else {
      // Regular users can only see their own resources
      userResources = resources.filter(resource => resource.ownerId === req.user.id);
    }
    
    res.json(userResources);
  } catch (err) {
    console.error(err);
    res.status(500).json({ message: 'Server error' });
  }
};

exports.getResourceById = (req, res) => {
  try {
    const resource = resources.find(r => r.id === parseInt(req.params.id));
    
    // Check if resource exists
    if (!resource) {
      return res.status(404).json({ message: 'Resource not found' });
    }
    
    // Check if user has permission to access this resource
    if (req.user.role !== 'admin' && resource.ownerId !== req.user.id) {
      return res.status(403).json({ message: 'Not authorized to access this resource' });
    }
    
    res.json(resource);
  } catch (err) {
    console.error(err);
    res.status(500).json({ message: 'Server error' });
  }
};