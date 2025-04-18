// SPDX-License-Identifier: MIT
pragma solidity >=0.8.0;
import "@openzeppelin/contracts/security/ReentrancyGuard.sol";

contract SkillsConnect is ReentrancyGuard{

    struct Project {
        uint256 id;
        string name;
        address creator;
        uint256 deadline;
        address bidder;
        uint256 amount;
        bool issubmitted;
        string filelocation;
        bool isfinished;
    }

    uint256 private projectCounter;
    address public owner;

    mapping(uint256 => Project) public projects;

    event ProjectCreated(uint256 indexed id,string name,address indexed creator,uint256 deadline);
    event AcceptBid(uint256 indexed id,address indexed bidder,uint256 amount);
    event SetUploaded(uint256 indexed id);
    event FinishedProject(uint256 indexed id);
    event RefundedProject(uint256 indexed id);

    constructor()
    {
        owner = msg.sender;
        projectCounter = 0;
    }

    modifier OnlyOwner()
    {
        require(msg.sender == owner,"Must be Owner");
        _;
    }

    modifier OnlyCreator(uint256 _id)
    {
        require(projects[_id].creator == msg.sender,"Must be Creator");
        _;
    }

    modifier Onlybidder(uint256 _id)
    {
        require(projects[_id].bidder == msg.sender,"Must be Accepted Bidder");
        _;
    }

    modifier NotCreator(uint256 _id,address _bidder)
    {
        require(projects[_id].creator != _bidder,"Cant Bid Your Own");
        _;
    }

    modifier GreaterThanZero(uint256 _amount)
    {
        require(_amount > 0,"Must be geater than zero");
        _;
    }

    modifier PredefinedAmount(uint256 _id)
    {
        require(projects[_id].amount == msg.value,"Must same as Fee");
        _;
    }

    function  createProject(string memory _name,uint256 _duration) external returns (uint256) 
    {
        uint256 _deadline = block.timestamp + _duration;
        projects[projectCounter] = Project({
            id : projectCounter,
            name :  _name,
            creator : msg.sender,
            bidder : address(0x000),
            amount : 0,
            issubmitted : false,
            filelocation : "",
            isfinished : false,
            deadline : _deadline
        });
        projectCounter++;

        emit ProjectCreated(projectCounter-1, _name, msg.sender, _deadline);
        return projectCounter-1;
    }

    function acceptBid(uint256 _id,uint256 _amount,address _bidder) external  nonReentrant OnlyCreator(_id) NotCreator(_id,_bidder) GreaterThanZero(_amount) payable returns (uint256)
    {
        Project storage p = projects[_id];
        p.amount = _amount;
        p.bidder = _bidder;

        require(_amount == msg.value,"Must be Same Amount");

        emit AcceptBid(_id, _bidder, _amount);
        return p.amount;
    }

    function finishProject(uint256 _id) external  nonReentrant OnlyCreator(_id) returns (uint256)
    {
        Project storage p = projects[_id];
        require(p.issubmitted,"File is not uploaded");
        require(!p.isfinished,"Already Set finished");
        p.isfinished = true;
        payable(p.bidder).transfer(p.amount);

        emit FinishedProject(_id);

        return _id;
    }

    function refundProject(uint256 _id) external  nonReentrant OnlyCreator(_id) returns (uint256)
    {
        Project storage p = projects[_id];
        require(!p.issubmitted,"File is  uploaded");
        require(!p.isfinished,"Already Set finished");
        p.isfinished = true;
        payable(p.creator).transfer(p.amount);

        emit RefundedProject(_id);

        return _id;
    }

    function setUploaded(uint256 _id,string memory _location) external  Onlybidder(_id) returns (uint256)
    {
        Project storage p = projects[_id];
        require(p.deadline > block.timestamp,"Deadline already met");
        require(!p.isfinished,"Already Set finished");
        p.filelocation = _location;
        p.issubmitted = true;

        emit SetUploaded(_id);
        return _id;
    }

    function rejectFile(uint256 _id) external  OnlyCreator(_id) returns (uint256)
    {
        Project storage p = projects[_id];
        require(!p.isfinished,"Already Set finished");
        p.issubmitted = false;
        p.filelocation = "";
        return _id;
    }

    function updateDeadline(uint256 _id,uint256 _duration) external  OnlyCreator(_id) returns (uint256)
    {
        Project storage p = projects[_id];
        require(!p.isfinished,"Already Set finished");
        p.deadline = p.deadline + _duration;

        return p.deadline;
    }

    receive() external payable {
        revert("Use the acceptBid function to send Ether");
    }

}