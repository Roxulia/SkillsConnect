import { useBlockchain } from "../contexts/BlockchainContext";
import { useNavigate,useLocation } from "react-router-dom";
import { BrowserProvider, Contract, parseEther } from "ethers";
import { useEffect ,useState } from "react";
import axios from "axios";
import Swal from "sweetalert2";

function ProjectDetail()
{
    const location = useLocation();
    const data = location.state;
    const navigate = useNavigate();
    const [project,setProject] =useState(null);
    const [user,setUser] = useState(null);
    const [bidders,setBidders] = useState([]);
    const [pid,setPid] = useState(data.id);
    const [description,setDescription] = useState("");
    const [amount,setAmount] = useState(0);
    const [duration,setDuration] = useState("");
    const {account,contract} = useBlockchain();

    useEffect(()=>{
        if(!account)
        {
            navigate("/");
        }

        axios.get("http://localhost:8080/api/user/me", { withCredentials: true })
                .then(response => {
                    console.log(response.data);
                setUser(response.data);
                })
                .catch(error => console.error("Error fetching user data:", error));

        axios.put(`http://localhost:8080/api/project/${data.id}`, null, {
            withCredentials: true,
          })
          .then((res) => {
            console.log(res.data);    
            setProject(res.data);
          } )
          .catch(error => alert(`Error fetching project: ` + error.response.data));

        axios.put(`http://localhost:8080/api/bid/project/${data.id}`, null, {
            withCredentials: true,
          })
          .then((res) => {
            console.log(res.data);
                setBidders(res.data);
          } )
          .catch(error => alert(`Error fetching project: ` + error.response.data));
    },[data,account,navigate])

    const handleAccept= async(bid) => {
        console.log(contract);
        console.log(bid.amount);
        try {
            Swal.fire({
                                title: "Processing...",
                                text: "Please wait for blockchain confirmation.",
                                allowOutsideClick: false,
                                allowEscapeKey: false,
                                didOpen: () => {
                                    Swal.showLoading();
                                }
                            });
            if (!contract) {
                throw new Error("Smart contract instance not found. Make sure your wallet is connected.");
            }
    
            console.log("Submitting transaction to blockchain...");
    
            const tx = await contract.acceptBid(project.id-1, parseEther(bid.amount.toString()), bid.bidderWallet, { value: parseEther(bid.amount.toString()) });
            if (!tx || !tx.wait) {
                throw new Error("Transaction failed or returned an unexpected result.");
            }
    
            console.log("Transaction sent, waiting for confirmation...");
            await tx.wait(); // Wait for transaction confirmation
            console.log("Transaction confirmed!");
    
            // Proceed with backend request
            const response = await axios.put(`http://localhost:8080/api/bid/${bid.id}/accept`, null, {
                params: { trxHash: tx.hash },
                withCredentials: true,
              });
    
            if (response.status === 200) {
                Swal.fire({
                                        icon: "success",
                                        title: "Success!",
                                        text: "Bid accepted successfully!",
                                    });
            } else {
                Swal.fire({
                                        icon: "error",
                                        title: "Error!",
                                        text: "Fail in backend!",
                                    });
            }
        } catch (error) {
            console.error("Error accepting bid : ", error.message);
            Swal.fire({
                icon: "error",
                title: "Error!",
                text: error.message
            });
        }
        finally {
            //window.location.reload();
        }
    }

    const handleSubmit = async(e) =>{
        e.preventDefault();
        try{
            const response = await axios.post("http://localhost:8080/api/bid/create", {
                pid,
                description,
                amount
            }, { withCredentials: true });
    
            if (response.status === 200) {
                alert("Bid created successfully!");
            } else {
                alert("Failed to create project in backend.");
            }
        } catch (error) {
            console.error("Error creating project:", error.message);
            alert(`Error: ${error.message}`);
        }
        finally {
            //window.location.reload();
        }
    }

    const extendDeadline = async(e)=>{
        e.preventDefault();
        console.log(contract);
        try {
            Swal.fire({
                title: "Processing...",
                text: "Please wait for blockchain confirmation.",
                allowOutsideClick: false,
                allowEscapeKey: false,
                didOpen: () => {
                    Swal.showLoading();
                }
            });
            if (!contract) {
                throw new Error("Smart contract instance not found. Make sure your wallet is connected.");
            }
    
            console.log("Submitting transaction to blockchain...");
    
            const tx = await contract.updateDeadline(project.id-1, duration * 24 * 3600);
            if (!tx || !tx.wait) {
                throw new Error("Transaction failed or returned an unexpected result.");
            }
    
            console.log("Transaction sent, waiting for confirmation...");
            await tx.wait(); // Wait for transaction confirmation
            console.log("Transaction confirmed!");
    
            // Proceed with backend request
            const response = await axios.put(`http://localhost:8080/api/project/${project.id}/extend`, null, {
                params: { duration: duration },
                withCredentials: true,
              });
    
            if (response.status === 200) {
                Swal.fire({
                    icon: "success",
                    title: "Success!",
                    text: "Duration Extended successfully!!"
                });
            } else {
                Swal.fire({
                    icon: "error",
                    title: "Error!",
                    text: "Fail in backend"
                });
            }
        } catch (error) {
            console.error("Error creating project:", error.message);
            Swal.fire({
                icon: "error",
                title: "Error!",
                text: error.message
            });
        }
        finally {
            window.location.reload();
        }
    }
    

    if (user && project && user.id === project.uid) {
        return (
            <div className="p-6 bg-neutral-950 max-h-screen">
                <h1 className="text-3xl font-extrabold text-sky-500 mb-4">{project.name}</h1>
                <p className="text-sky-300"><strong>Details:</strong> {project.detail}</p>
                <p className="text-sky-300"><strong>Category:</strong> {project.category}</p>
                <p className="text-sky-300"><strong>Deadline:</strong> {project.deadline}</p>
        
                <hr className="my-4" />
    
                {project.status !== 'HIRING' ? (
                    <>
                    <h2 className="text-2xl font-semibold text-sky-500 mb-2">Bids status</h2>
                    <div className="p-4 bg-red-100 text-red-700 rounded-lg text-center font-semibold">
                        Bidder Already Accepted
                    </div>
                    </>
                ) : (
                    <div>
                        <h2 className="text-2xl font-semibold text-sky-500 mb-2">Bidders</h2>
                        <ul className="space-y-4">
                            {bidders.map(b => (
                                <div key={b.bidderId} className="p-6  rounded-lg shadow-md border border-gray-200 transition duration-300 hover:shadow-lg">
                                    <div>
                                        {/* Bidder Details */}
                                        <h3 className="text-lg font-bold text-sky-500">{b.bidder}</h3>
                                        <p className="text-sky-300 mt-1">{b.description}</p>
                                        <p className="text-green-600 font-semibold text-xl mt-2">{b.amount} ETH</p>

                                        {/* Action Buttons */}
                                        <div className="flex gap-2 mt-4">
                                            <button
                                                className="w-1/2 ring-2 ring-green-500 text-white font-semibold py-2 rounded-lg hover:bg-gray-700 hover:shadow-[0_0_5px_#4ADE80,0_0_10px_#4ADE80,0_0_20px_#4ADE80] transition duration-300"
                                                onClick={() => handleAccept(b)}
                                            >
                                                Accept
                                            </button>
                                            <button
                                                className="w-1/2 ring-2 ring-blue-500 text-white font-semibold py-2 rounded-lg hover:bg-gray-700 hover:shadow-[0_0_5px_#06B6D4,0_0_10px_#06B6D4,0_0_20px_#06B6D4] transition duration-300"
                                                onClick={() => navigate("/userprofile", { state: { uid: b.bidderId } })}
                                            >
                                                View Profile
                                            </button>
                                        </div>
                                    </div>
                                </div>
                            ))}
                        </ul>
    
                        <h2 className="text-2xl font-semibold text-sky-500 mt-6">Extend Deadline</h2>
                        <form onSubmit={extendDeadline} className="space-y-4">
                            <input
                                type="number"
                                placeholder="Duration (Day)"
                                value={duration}
                                onChange={(e) => setDuration(e.target.value)}
                                required
                                min={1}
                                className="w-full p-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                            />
                            <button
                                type="submit"
                                className="w-full  ring-2 ring-blue-600 text-white font-semibold py-3 rounded-lg hover:bg-gray-700 hover:shadow-[0_0_5px_#06B6D4,0_0_10px_#06B6D4,0_0_20px_#06B6D4] transition duration-300"
                            >
                                Extend
                            </button>
                        </form>
                    </div>
                )}
                <div className="mt-6">
                                        <button
                                            onClick={() => navigate("/userfeed", { state: user })}
                                            className="w-full ring-2 ring-blue-500 text-white font-semibold py-3 rounded-xl hover:bg-gray-700 hover:shadow-[0_0_5px_#06B6D4,0_0_10px_#06B6D4,0_0_20px_#06B6D4] transition duration-300"
                                        >
                                            Back to User Feed
                                        </button>
                </div>
            </div>
        );
    }
    
    if (!project) {
        return <div className="text-center text-gray-500">Loading...</div>;
    }
    
    return (
        <div className="p-6 bg-neutral-950 max-h-screen">
            <h1 className="text-3xl font-extrabold text-sky-500 mb-4">{project.name}</h1>
            <p className="text-sky-300"><strong>Details:</strong> {project.detail}</p>
            <p className="text-sky-300"><strong>Category:</strong> {project.category}</p>
            <p className="text-sky-300"><strong>Deadline:</strong> {project.deadline}</p>
    
            <hr className="my-4" />
    
            <h2 className="text-2xl font-semibold text-sky-500">Bidders</h2>
            <ul className="space-y-4">
                {bidders.map(b => (
                    <div key={b.bidderId} className="p-6 rounded-lg shadow-md border border-gray-200 transition duration-300 hover:shadow-lg">
                        <div className="flex justify-between items-center">
                            {/* Bidder Details */}
                            <div>
                                <h3 className="text-lg font-bold text-sky-500">{b.bidder}</h3>
                                <p className="text-sky-300 mt-1">{b.description}</p>
                            </div>

                            {/* Bid Amount */}
                            <span className="text-green-500 font-semibold text-xl">{b.amount} ETH</span>
                        </div>
                    </div>
                ))}
            </ul>
            <hr className="my-4"/>
            {project.status !== 'HIRING' ? (
                    <>
                    <h2 className="text-2xl font-semibold text-sky-500">Bid Status</h2>
                    <div className="p-4 bg-red-100/70 text-red-600 rounded-lg text-center font-semibold">
                        Bidder is  Already Accepted
                    </div></>
                ) :(<div><h2 className="mb-4 text-2xl font-semibold text-sky-500 mt-6">Create Bid</h2>
            <form onSubmit={handleSubmit} className="space-y-4 m-y-4">
                <textarea
                    placeholder="Bid Description"
                    value={description}
                    onChange={(e) => setDescription(e.target.value)}
                    required
                    className="w-full p-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 h-24 resize-none"
                />
                <input
                    type="number"
                    placeholder="Amount (ETH)"
                    value={amount}
                    onChange={(e) => setAmount(e.target.value)}
                    required
                    min={0.0}
                    step={0.001}
                    className="w-full p-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
                <button
                    type="submit"
                    className=" my-3 w-full ring-2 ring-blue-500 hover:shadow-[0_0_5px_#06B6D4,0_0_10px_#06B6D4,0_0_20px_#06B6D4]  text-white font-semibold py-3 rounded-lg hover:bg-gray-700 transition duration-300"
                >
                    Create Bid
                </button>
            </form></div>)}
            <div className="mt-6">
                        <button
                            onClick={() => navigate("/userfeed", { state: user })}
                            className="w-full ring-2 ring-blue-500 hover:shadow-[0_0_5px_#04B6D4,0_0_10px_#04B6D4,0_0_20px_#06B6D4] text-white font-semibold py-3 rounded-xl  hover:bg-gray-700 transition duration-300"
                        >
                            Back to User Feed
                        </button>
            </div>
        </div>
    );
    
}

export default ProjectDetail;