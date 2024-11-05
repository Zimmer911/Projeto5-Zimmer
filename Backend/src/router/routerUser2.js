const {Router} = require("express");
const User2Controller = require("../controller/User2Controller");
const { validateUser2, validateUser2Id } = require("../middlewares/ValidateUser2");

const router = Router();

router.post('/', validateUser2 ,(req,res) => {
    User2Controller.create(req,res)
});
router.put('/:id',validateUser2,validateUser2Id,(req,res) => {
    User2Controller.update(req,res)
});
router.get('/',(req,res) => {
    User2Controller.getAll(req,res)
});
router.get('/:id',validateUser2Id,(req,res) => {
    User2Controller.getOne(req,res)
});
router.delete('/:id',validateUser2Id,(req,res) => {
    User2Controller.delete(req,res)
});

router.post('/login', (req, res) => {
    User2Controller.login(req, res)
  });
  
module.exports = router;