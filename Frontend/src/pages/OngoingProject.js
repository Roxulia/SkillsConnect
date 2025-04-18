import { useBlockchain } from "../contexts/BlockchainContext";
import { useNavigate,useLocation } from "react-router-dom";
import { BrowserProvider, Contract, parseEther } from "ethers";
import { useEffect ,useState } from "react";
import axios from "axios";
import Swal from "sweetalert2";

function OngoingProject(){

    const location = useLocation();
    const data = location.state;
    const navigate = useNavigate();
    const [project,setProject] =useState(null);
    const [creator,setCreator] = useState(null);
    const [duration,setDuration] = useState(0);
    const [user,setUser] = useState(null);
    const {account,contract} = useBlockchain();
    const [file,setFile] = useState(null);
    const [uploading, setUploading] = useState(false);
    const [loading,setLoading] = useState(false);

    const handleFileChange = (event) => {
        setFile(event.target.files[0]);
    };

    useEffect(()=>{
            if(!account)
            {
                navigate("/");
            }
            if (!data?.id) return;
        
            axios.get("http://localhost:8080/api/user/me", { withCredentials: true })
                        .then(response => {
                            //console.log(response.data);
                        setUser(response.data);
                        })
                        .catch(error => console.error("Error fetching user data:", error));
        
            axios.put(`http://localhost:8080/api/project/${data.id}`, null, {
                    withCredentials: true,
                  })
                  .then((res) => {
                    //console.log(res.data);    
                    setProject(res.data);
                  } )
                  .catch(error => alert(`Error fetching project: ` + error.response.data));
            
            axios.get(`http://localhost:8080/api/user/${data.uid}`, {
                    withCredentials: true,
                  })
                  .then((res) => {
                    //console.log(res.data);    
                    setCreator(res.data);
                  } )
                  .catch(error => alert(`Error fetching project: ` + error.response.data));
    },[account,navigate,data]);

    const downloadFile = async()=>{
        if(project.location != "")
        {
            try{
                const response = await axios.put(`http://localhost:8080/api/project/${data.id}/getFile`,null, { responseType: "blob",withCredentials: true });
        
                const url = window.URL.createObjectURL(new Blob([response.data]));
                const link = document.createElement("a");
                link.href = url;
                link.setAttribute("download", "project_file.zip"); // Change extension accordingly
                document.body.appendChild(link);
                link.click();
                link.remove();
            } catch (error) {
                console.error("Download failed", error);
                alert(error.response?.data || "Download failed");
            }
            return;
        }
        alert("file is not uploaded yet");
        
    }

    const uploadFile = async()=>{
        if (!file) return alert("Please select a file");
        const formData = new FormData();
        formData.append("file", file);

        try {
        setUploading(true);
        Swal.fire({
            title: "Processing...",
            text: "Please wait while the file is being uploaded.",
            allowOutsideClick: false,
            allowEscapeKey: false,
            didOpen: () => {
                Swal.showLoading();
            }
        });
        const response = await axios.post(
            `http://localhost:8080/api/project/${project.id}/filesubmit`,
            formData,
            { withCredentials: true, headers: { "Content-Type": "multipart/form-data" } }
        );
        const location = response.data;
            try {
                if (!contract) {
                    throw new Error("Smart contract instance not found. Make sure your wallet is connected.");
                }
        
                console.log("Submitting transaction to blockchain...");
        
                const tx = await contract.setUploaded(project.id-1,location);
                if (!tx || !tx.wait) {
                    throw new Error("Transaction failed or returned an unexpected result.");
                }
        
                console.log("Transaction sent, waiting for confirmation...");
                await tx.wait(); // Wait for transaction confirmation
                console.log("Transaction confirmed!");
                Swal.fire({
                    icon: "success",
                    title: "Success!",
                    text: "File uploaded successfully!",
                });
    
            }
            catch(error)
            {
                Swal.fire({
                    icon: "error",
                    title: "Error",
                    text: error.message,
                });
            }
        } catch (error) {
            Swal.fire({
                icon: "error",
                title: "Error",
                text: error.message,
            });
        } finally {
        setUploading(false);
        }
        
    }
    
    const setFinished = async()=>{
        if(project.loaction != "")
        {
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
        
                const tx = await contract.finishProject(project.id-1);
                if (!tx || !tx.wait) {
                    throw new Error("Transaction failed or returned an unexpected result.");
                }
        
                console.log("Transaction sent, waiting for confirmation...");
                await tx.wait(); // Wait for transaction confirmation
                console.log("Transaction confirmed!");
        
                // Proceed with backend request
                const response = await axios.put(`http://localhost:8080/api/project/${project.id}/finished`,null,{
                    withCredentials: true,
                  });
        
                if (response.status === 200) {
                    Swal.fire({
                        icon: "success",
                        title: "Success!",
                        text: "Fund transfered successfully!",
                    });
                    
                } else {
                    Swal.fire({
                        icon: "error",
                        title: "Error!",
                        text: "Failed to save transaction record in backend dont worry ur funds are safe!",
                    });
                }
            } catch (error) {
                console.error("Error editing project:", error.message);
                Swal.fire({
                    icon: "error",
                    title: "Error",
                    text: error.message,
                });
            }
            finally {
                //window.location.reload();
            }
            return;
        }
        alert("File is not uploaded yet");
    }

    const setRefunded = async()=>{
        if(project.status != 'EXPIRED')
        {
            alert("Deadline not met yet");
        }
        else
        {
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
        
                const tx = await contract.refundProject(project.id-1);
                if (!tx || !tx.wait) {
                    throw new Error("Transaction failed or returned an unexpected result.");
                }
        
                console.log("Transaction sent, waiting for confirmation...");
                await tx.wait(); // Wait for transaction confirmation
                console.log("Transaction confirmed!");
        
                // Proceed with backend request
                const response = await axios.put(`http://localhost:8080/api/project/${project.id}/refunded`,null,{
                    withCredentials: true,
                  });
        
                if (response.status === 200) {
                    Swal.fire({
                        icon: "success",
                        title: "Success!",
                        text: "Fund transfered successfully!",
                    });
                    //navigate("/giverating",{state:{uid:}})
                } else {
                    Swal.fire({
                        icon: "error",
                        title: "Error!",
                        text: "Failed to save transaction record in backend dont worry ur funds are safe!",
                    });
                }
            } catch (error) {
                console.error("Error creating project:", error.message);
                Swal.fire({
                    icon: "error",
                    title: "Error",
                    text: error.message,
                });
            }
            finally {
                //window.location.reload();
            }
        }
    }

    const setFileRejected = async()=>{

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

                const tx = await contract.rejectFile(project.id-1);
                if (!tx || !tx.wait) {
                    throw new Error("Transaction failed or returned an unexpected result.");
                }

                console.log("Transaction sent, waiting for confirmation...");
                await tx.wait(); // Wait for transaction confirmation
                console.log("Transaction confirmed!");

                // Proceed with backend request
                const response = await axios.put(`http://localhost:8080/api/project/${project.id}/file-reject`,null,{
                    withCredentials: true,
                });

                if (response.status === 200) {
                    Swal.fire({
                        icon: "success",
                        title: "Success!",
                        text: "File rejected successfully!",
                    });
                    //navigate("/giverating",{state:{uid:}})
                } else {
                    Swal.fire({
                        icon: "error",
                        title: "Error!",
                        text: "Failed in backend!",
                    });
                }
            } catch (error) {
                console.error("Error rejecting project file:", error.message);
                Swal.fire({
                    icon: "error",
                    title: "Error",
                    text: error.message,
                });
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
                    text: "Duration extended successfully!",
                });
            } else {
                Swal.fire({
                    icon: "error",
                    title: "Error!",
                    text: "Failed in backend!",
                });
            }
        } catch (error) {
            console.error("Error creating project:", error.message);
            console.error("Error rejecting project file:", error.message);
                Swal.fire({
                    icon: "error",
                    title: "Error",
                    text: error.message,
                });
        }
        finally {
            setDuration("");
        }
    }

    if (!project || !creator) {
        return <div className="text-center text-sky-600 text-lg">Loading...</div>;
    }
    
    if (user && project && user.id === project.uid) {
        return (
            <div className="p-6 bg-neutral-950 max-h-screen">
                <h1 className="text-3xl font-extrabold text-sky-500 mb-4">{project.name}</h1>
                <p className="text-sky-300"><strong>Details:</strong> {project.detail}</p>
                <p className="text-sky-300"><strong>Category:</strong> {project.category}</p>
                <p className="text-sky-300"><strong>Deadline:</strong> {project.deadline}</p>
        
                <hr className="my-4" />

                {/* File Download Section */}
                <div className="mb-3 p-2 border border-gray-200 pt-4">
                    <h2 className="text-xl font-semibold text-sky-500">File Location</h2>
                    <p className="text-sky-300 break-all">{project.location}</p>
                    {project.location && (
                        <button
                            onClick={downloadFile}
                            className="mt-2 w-full ring-2 ring-green-500 text-white font-semibold py-3 rounded-lg hover:bg-gray-700 hover:shadow-[0_0_5px_#4ADE80,0_0_10px_#4ADE80,0_0_20px_#4ADE80] transition duration-300"
                        >
                            Download File
                        </button>
                    )}
                </div>

                {project.status == 'ONGOING' &&(<div className="mb-3 p-2 border border-gray-200 pt-4">
                    <h2 className="text-xl font-semibold text-sky-500">Extend Deadline</h2>
                    <form onSubmit={extendDeadline} className="space-y-4">
                        <input
                            type="number"
                            placeholder="Duration (Days)"
                            value={duration}
                            onChange={(e) => setDuration(e.target.value)}
                            required
                            className="w-full p-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                        />
                        <button
                            type="submit"
                            className="w-full ring-2 ring-blue-600 text-white font-semibold py-3 rounded-lg hover:bg-gray-700 hover:shadow-[0_0_5px_#06B6D4,0_0_10px_#06B6D4,0_0_20px_#06B6D4] transition duration-300"
                        >
                            Extend Deadline
                        </button>
                    </form>
                </div>)}
    
                <div className="mb-3 p-2 border border-gray-200 pt-4">
                    <h2 className="text-xl font-semibold text-sky-500">Project Completion</h2>
                    {project.status == "FINISHED" && (<><h2 className="text-xl w-full bg-red-100 text-red-500 font-bold">Already Finished or Refunded</h2></>)}
                    <div className="grid grid-cols-2 gap-4">
                        {project.filestatus == "PENDING" && (<><button
                            onClick={setFinished}
                            className="w-full ring-2 ring-green-500 text-white font-semibold py-3 rounded-lg hover:bg-gray-700 hover:shadow-[0_0_5px_#4ADE80,0_0_10px_#4ADE80,0_0_20px_#4ADE80] transition duration-300"
                        >
                            Set Finish
                        </button>
                            <button
                            onClick={setFileRejected}
                        className="w-full ring-2 ring-red-500 text-white font-semibold py-3 rounded-lg hover:bg-gray-700 hover:shadow-[0_0_5px_#D46606,0_0_10px_#D46606,0_0_20px_#D46606] transition duration-300"
                    >
                        Reject File
                    </button></>)
                        }
                    </div>
                    {project.status=="EXPIRED" && (project.filestatus == "REJECTED" || !project.location) && (<button
                        onClick={setRefunded}
                        className="w-full ring-2 ring-green-600 text-white font-semibold py-3 rounded-lg hover:bg-gray-700 hover:shadow-[0_0_5px_#4ADE80,0_0_10px_#4ADE80,0_0_20px_#4ADE80] transition duration-300"
                    >
                        Refund
                    </button>)}
                </div>
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
    
    return (
    <div className="p-6 bg-neutral-950 max-h-screen">
        <div>
        <h1 className="text-3xl font-extrabold text-sky-500 mb-4">{project.name}</h1>
        <p className="text-sky-300"><strong>Details:</strong> {project.detail}</p>
        <p className="text-sky-300"><strong>Category:</strong> {project.category}</p>
        <p className="text-sky-300"><strong>Deadline:</strong> {project.deadline}</p>

        <hr className="my-4" />


                {/* Creator Contact Details */}
                <h2 className="text-2xl font-bold text-sky-500 mb-4">Creator Contacts</h2>
                <p className="text-sky-300 mb-2">
                    <strong>Name:</strong> {creator.name}
                </p>
                <p className="text-sky-300 mb-2">
                    <strong>Email:</strong> {creator.email}
                </p>
                <p className="text-sky-300 mb-2">
                    <strong>Phone:</strong> {creator.phone}
                </p>
                <p className="text-sky-300 mb-2">
                    <strong>Wallet:</strong> {creator.wallet}
                </p>
    
                {/* File Upload Section */}
                {(project.status ==="CHECKING" || project.status ==="ONGOING") ? (
                    <div className="mb-4">
                        <label className="block text-sky-500 font-medium mb-1">Upload File</label>
                        <input
                            type="file"
                            onChange={handleFileChange}
                            className="w-full text-white border border-gray-300 p-2 rounded focus:outline-none focus:ring-2 focus:ring-blue-500"
                        />
                        <button
                            onClick={uploadFile}
                            disabled={uploading}
                            className="w-full mt-2 ring-2 ring-blue-500 hover:shadow-[0_0_5px_#04B6D4,0_0_10px_#04B6D4,0_0_20px_#06B6D4] text-white font-semibold py-3 rounded-xl  hover:bg-gray-700 transition duration-300 disabled:bg-gray-400"
                        >
                            {uploading ? "Uploading..." : "Upload File"}
                        </button>
                    </div>
                ) : (
                    <div>
                        <h2 className="text-xl bg-red-100 text-red-700 font-bold">Project Finished or Refunded!!</h2>
                    </div>
                )}
                <div className="mt-6">
                                                    <button
                                                        onClick={() => navigate("/userfeed", { state: user })}
                                                        className="w-full ring-2 ring-blue-500 hover:shadow-[0_0_5px_#04B6D4,0_0_10px_#04B6D4,0_0_20px_#06B6D4] text-white font-semibold py-3 rounded-xl  hover:bg-gray-700 transition duration-300"
                                                    >
                                                        Back to User Feed
                                                    </button>
                            </div>
            </div>

        </div>
    );
    
}

export default OngoingProject;