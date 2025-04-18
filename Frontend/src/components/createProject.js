import { useBlockchain } from "../contexts/BlockchainContext";
import { useNavigate } from "react-router-dom";
import { useEffect, useState } from "react";
import axios from "axios";
import Swal from "sweetalert2";


function CreateProject({ user }) {
    const [name, setName] = useState("");
    const [current_user, setUser] = useState(user);
    const [detail, setDetail] = useState("");
    const [duration, setDuration] = useState("");
    const [category, setCategory] = useState("");
    const [categoryList, setCategoryList] = useState([]);
    const [project, setProject] = useState([]);
    const [ongoingproject, setOngoing] = useState([]);
    const [checking, setChecking] = useState([]);
    const { contract, account } = useBlockchain();
    const navigate = useNavigate();

    // State to manage the active tab
    const [activeTab, setActiveTab] = useState("create");

    useEffect(() => {
        if (!account) {
            navigate("/");
        }

        axios.get("http://localhost:8080/api/user/me", { withCredentials: true })
            .then(response => setUser(response.data))
            .catch(error => console.error("Error fetching user data:", error));

        axios.get("http://localhost:8080/api/category")
            .then(response => setCategoryList(response.data))
            .catch(error => console.error("Error fetching category:", error));

        axios.put(`http://localhost:8080/api/project/${current_user.id}/projects`, null, { withCredentials: true })
            .then((res) => setProject(res.data))
            .catch(error => alert(`Error fetching project: ` + error.response.data));

        axios.put(`http://localhost:8080/api/project/${current_user.id}/ongoings`, null, { withCredentials: true })
            .then((res) => setOngoing(res.data))
            .catch(error => alert(`Error fetching project: ` + error.response.data));

        axios.put(`http://localhost:8080/api/project/${current_user.id}/checkings`, null, { withCredentials: true })
            .then((res) => setChecking(res.data))
            .catch(error => alert(`Error fetching project: ` + error.response.data));

    }, [account,navigate,user]);

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            Swal.fire({
                                title: "Processing...",
                                text: "Please wait while project being created!!",
                                allowOutsideClick: false,
                                allowEscapeKey: false,
                                didOpen: () => {
                                    Swal.showLoading();
                                }
                            });
            if (!contract) {
                throw new Error("Smart contract instance not found. Make sure your wallet is connected.");
            }

            const tx = await contract.createProject(name, duration * 24 * 3600);
            if (!tx || !tx.wait) {
                throw new Error("Transaction failed or returned an unexpected result.");
            }

            await tx.wait();

            const response = await axios.post("http://localhost:8080/api/project/create", {
                name,
                detail,
                duration,
                category
            }, { withCredentials: true });

            if (response.status === 200) {
                Swal.fire({
                                                        icon: "success",
                                                        title: "Success!",
                                                        text: "Project created successfully!",
                                                    });
                setProject((prevProjects) => [...prevProjects, response.data]);
                setName("");
                setDetail("");
                setDuration("");
                setCategory("");
            } else {
                Swal.fire({
                                                        icon: "error",
                                                        title: "Error!",
                                                        text: "Failed in Backend!!",
                                                    });
            }
        } catch (error) {
            console.error("Error creating project:", error.message);
            Swal.fire({
                                                    icon: "error",
                                                    title: "Error!",
                                                    text: error.message,
                                                });
        }
    };

    return (
        <div className="p-6">
            {/* Tab Navigation */}
            <div className="flex space-x-4 mb-4">
                <button
                    onClick={() => setActiveTab("create")}
                    className={`pb-2 font-medium ${activeTab === "create" ? "border-b-2 border-blue-500 text-blue-500" : "text-gray-200 hover:text-blue-500"}`}
                >
                    Create Project
                </button>
                <button
                    onClick={() => setActiveTab("ongoing")}
                    className={`pb-2 font-medium ${activeTab === "ongoing" ? "border-b-2 border-blue-500 text-blue-500" : "text-gray-200 hover:text-blue-500"}`}
                >
                    Ongoing Projects
                </button>
                <button
                    onClick={() => setActiveTab("checking")}
                    className={`pb-2 font-medium ${activeTab === "checking" ? "border-b-2 border-blue-500 text-blue-500" : "text-gray-200 hover:text-blue-500"}`}
                >
                    Projects Needing Confirmation
                </button>
                <button
                    onClick={() => setActiveTab("created")}
                    className={`pb-2 font-medium ${activeTab === "created" ? "border-b-2 border-blue-500 text-blue-500" : "text-gray-200 hover:text-blue-500"}`}
                >
                    Created Projects
                </button>
            </div>

            {/* Tab Content */}
            <div>
                {activeTab === "create" && (
                    <div>
                        <h2 className="text-xl text-sky-400 font-bold mb-4">Create Project</h2>
                        <form onSubmit={handleSubmit} className="space-y-4">
                            <input
                                type="text"
                                placeholder="Project Name"
                                value={name}
                                onChange={(e) => setName(e.target.value)}
                                required
                                maxLength={100}
                                className="w-full p-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                            />
                            <textarea
                                placeholder="Project Detail"
                                value={detail}
                                onChange={(e) => setDetail(e.target.value)}
                                required
                                className="w-full p-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 h-24 resize-none"
                            />
                            <input
                                type="number"
                                placeholder="Duration (days)"
                                value={duration}
                                onChange={(e) => setDuration(e.target.value)}
                                min={1}
                                required
                                className="w-full p-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                            />
                            <select
                                value={category}
                                onChange={(e) => setCategory(e.target.value)}
                                className="w-full p-2 border rounded-lg focus:ring-2 focus:ring-blue-400 outline-none"
                                required
                            >
                                <option disabled>Select a category</option>
                                {categoryList.map((c) => (
                                    <option key={c.id} value={c.name}>
                                        {c.name}
                                    </option>
                                ))}
                            </select>
                            <button
                                type="submit"
                                className=" m-y-3 ring-2 ring-blue-500 shadow-[0_0_5px_#06B6D4,0_0_10px_#06B6D4,0_0_20px_#06B6D4] w-full text-white py-3 rounded-lg hover:bg-gray-700 transition"
                            >
                                Create Project
                            </button>
                        </form>
                    </div>
                )}

                {activeTab === "ongoing" && (
                    <div>
                        <h2 className="text-xl text-sky-400 font-bold mb-4">Ongoing Projects</h2>
                        {ongoingproject.length > 0 ? (
                            ongoingproject.map((p) => (
                                <div key={p.id} className="p-6 m-2 rounded-lg  border-2 border-gray-700 shadow-white">
                                    <div className="flex justify-between items-center text-sky-400">
                                        <div>
                                            <h3 className="text-lg font-bold">{p.name}</h3>
                                            <p className="text-cyan-500">{p.detail}</p>
                                            <p className="text-sm text-blue-400 mt-1">Category: {p.category}</p>
                                        </div>
                                        <button
                                            className=" mx-3 ring-2 ring-blue-600   insert-ring-2 insert-ring-blue-400 hover:bg-gray-700   text-white font-bold py-2 px-6 rounded-lg text-lg shadow-[0_0_5px_#06B6D4,0_0_10px_#06B6D4,0_0_20px_#06B6D4]"
                                            onClick={() => {
                                                navigate("/ongoingproject", { state: p })}
                                            }
                                        >
                                            Detail
                                        </button>
                                    </div>
                                </div>
                            ))
                        ) : (
                            <p className="text-sky-400">No ongoing projects.</p>
                        )}
                    </div>
                )}

                {activeTab === "checking" && (
                    <div>
                        <h2 className="text-xl text-sky-400 font-bold mb-4">Projects Needing Confirmation</h2>
                        {checking.length > 0 ? (
                            checking.map((p) => (
                                <div key={p.id} className="p-6 m-2 rounded-lg  border-2 border-gray-700 shadow-white">
                                    <div className="flex justify-between items-center text-sky-400">
                                        <div>
                                            <h3 className="text-lg font-bold">{p.name}</h3>
                                            <p className="text-cyan-500">{p.detail}</p>
                                            <p className="text-sm text-blue-400 mt-1">Category: {p.category}</p>
                                        </div>
                                        <button
                                            className=" mx-3 ring-2 ring-blue-600   insert-ring-2 insert-ring-blue-400 hover:bg-gray-700   text-white font-bold py-2 px-6 rounded-lg text-lg shadow-[0_0_5px_#06B6D4,0_0_10px_#06B6D4,0_0_20px_#06B6D4]"
                                            onClick={() => navigate("/ongoingproject", { state: p })}
                                        >
                                            Detail
                                        </button>
                                    </div>
                                </div>
                            ))
                        ) : (
                            <p className="text-sky-400">No projects needing confirmation.</p>
                        )}
                    </div>
                )}

                {activeTab === "created" && (
                    <div>
                        <h2 className="text-xl text-sky-400 font-bold mb-4">Created Projects</h2>
                        {project.length > 0 ? (
                            project.map((p) => (
                                <div key={p.id} className="p-6 m-2 rounded-lg  border-2 border-gray-700 shadow-white">
                                    <div className="flex justify-between items-center text-sky-400">
                                        <div>
                                            <h3 className="text-lg font-bold">{p.name}</h3>
                                            <p className="text-cyan-500">{p.detail}</p>
                                            <p className="text-sm text-blue-400 mt-1">Category: {p.category}</p>
                                        </div>
                                        <button
                                            className=" mx-3 ring-2 ring-blue-600   insert-ring-2 insert-ring-blue-400 hover:bg-gray-700   text-white font-bold py-2 px-6 rounded-lg text-lg shadow-[0_0_5px_#06B6D4,0_0_10px_#06B6D4,0_0_20px_#06B6D4]"
                                            onClick={() => {
                                                if(p.status == "HIRING") {
                                                    navigate("/projectdetail", { state: p })
                                                }
                                                else{
                                                    navigate("/ongoingproject", { state: p })
                                                }
                                                
                                            }}
                                        >
                                            Detail
                                        </button>
                                    </div>
                                </div>
                            ))
                        ) : (
                            <p className="text-sky-400">No created projects.</p>
                        )}
                    </div>
                )}
            </div>
        </div>
    );
}

export default CreateProject;