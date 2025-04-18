import { useBlockchain } from "../contexts/BlockchainContext";
import { useNavigate,useLocation } from "react-router-dom";
import { useEffect ,useState } from "react";
import axios from "axios";


function ViewHiringProjects({user}){
    const [hiringProjects, setHiringProjects] = useState([]);
    const [recommendedProjects, setRecommendedProjects] = useState([]);
    const navigate = useNavigate();
    const {account} = useBlockchain();

    useEffect(() => {
        const fetchProjects = async () => {
            try {
                const response = await axios.get("http://localhost:8080/api/project/hiring-projects", { withCredentials: true });
                setHiringProjects(response.data.hiring);
                if (response.data.recommended) {
                    setRecommendedProjects(response.data.recommended);
                }
            } catch (error) {
                console.error("Failed to fetch projects", error);
            }
        };
        fetchProjects();
    }, [account,navigate]);

    if(hiringProjects.length == 0)
    {
        return <p>No projects to showed</p>;
    }

    return (
        <div className="bg-neutral-950">
            {recommendedProjects.length > 0 && (
                <>
                <div className="my-3 p-3">
                    <h2 className="text-xl font-bold text-sky-400 mb-4">Recommended Projects</h2>
                    <ul className="space-y-3">
                        {recommendedProjects.map(project =>
                            project.uid !== user.id ? ( // Use strict !== for comparison
                                <div key={project.id} className="p-6  rounded-lg  border-2 border-gray-700 shadow-white">
                                <div className="flex justify-between items-center text-sky-400">
                                    <div>
                                        <h3 className="text-lg font-bold">{project.name}</h3>
                                        <p className="text-cyan-500">{project.detail}</p>
                                        <p className="text-sm text-blue-400 mt-1">Category: {project.category}</p>
                                    </div>
                                    <button
                                        className=" mx-3 ring-2 ring-blue-600   insert-ring-2 insert-ring-blue-400 hover:bg-gray-700   text-white font-bold py-2 px-6 rounded-lg text-lg shadow-[0_0_5px_#06B6D4,0_0_10px_#06B6D4,0_0_20px_#06B6D4]"
                                        onClick={() => navigate("/projectdetail", { state: project })}
                                    >
                                        Detail
                                    </button>
                                </div>
                            </div>
                            ) : null
                        )}

                    </ul>
                </div>
                <hr className="text-blue-500"/>
                </>
            )}
            
            <div className="p-3 my-3">
                <h2 className="text-xl font-bold text-sky-400 mb-4">Hiring Projects</h2>
                <ul className="space-y-3">
                    {hiringProjects.map(project => (
                        project.uid != user.id ? (
                    <div key={project.id} className="p-6  rounded-lg  border-2 border-gray-700 shadow-white">
                                <div className="flex justify-between items-center text-sky-400">
                                    <div>
                                        <h3 className="text-lg font-bold">{project.name}</h3>
                                        <p className="text-cyan-500">{project.detail}</p>
                                        <p className="text-sm text-blue-400 mt-1">Category: {project.category}</p>
                                    </div>
                                    <button
                                        className=" mx-3 ring-2 ring-blue-600   insert-ring-2 insert-ring-blue-400 hover:bg-gray-700   text-white font-bold py-2 px-6 rounded-lg text-lg shadow-[0_0_5px_#06B6D4,0_0_10px_#06B6D4,0_0_20px_#06B6D4]"
                                        onClick={() => navigate("/projectdetail", { state: project })}
                                    >
                                        Detail
                                    </button>
                                </div>
                            </div>
                 ): null

                    ))}
                </ul>
            </div>
        </div>
    );
}

export default ViewHiringProjects;