import { useBlockchain } from "../contexts/BlockchainContext";
import { useNavigate,useLocation } from "react-router-dom";
import { useEffect ,useState } from "react";
import axios from "axios";


const Register = () => {
    const [user, setUser] = useState(null);
    const [name, setName] = useState("");
    const [email, setEmail] = useState("");
    const [phone, setPhone] = useState("");
    const [skills, setSkills] = useState([]);
    const [selectedSkills, setSelectedSkills] = useState([]);
    const {account} = useBlockchain();
    const navigate = useNavigate();
  
    useEffect(() => {
      axios.get("http://localhost:8080/api/user/me", { withCredentials: true })
        .then(response => {
          setUser(response.data);
          setName(response.data.name);
          setEmail(response.data.email);
          setPhone(response.data.phone);
          setSelectedSkills(response.data.skillsId);
        })
        .catch(error => console.error("Error fetching user data:", error));
  
      axios.get("http://localhost:8080/api/skill")
        .then(response => setSkills(response.data))
        .catch(error => console.error("Error fetching skills:", error));
    }, [account,navigate]);
  
    const updateField = (field, value) => {
      axios.put(`http://localhost:8080/api/user/${user.id}/${field}`, null, {
        params: { [field]: value },
        withCredentials: true,
      })
      .then(() => alert(`${field} updated successfully!`))
      .catch(error => alert(`Error updating ${field}: ` + error.response.data));
    };
  
    const updateSkills = () => {
      console.log(selectedSkills);
      axios.post(`http://localhost:8080/api/user/${user.id}/skills`, selectedSkills,
        {withCredentials: true},
      )
      .then(() => alert("Skills updated successfully!"))
      .catch(error => alert("Error updating skills: " + error.response.data));
    };
  
    /*const updateProfileImage = (e) => {
      e.preventDefault();
      const formData = new FormData();
      formData.append("image", profileImage);
      axios.post(`http://localhost:8080/api/user/${user.id}/profile-image`, formData, {
        withCredentials: true,
      })
      .then(() => alert("Profile image updated successfully!"))
      .catch(error => alert("Error updating profile image: " + error.response.data));
    };*/

    const infoSave = () => {
        updateField("name", name);
        updateField("email", email);
        updateField("phone", phone);
        updateSkills();
        navigate("/userfeed");
    }

  
    if (!user) return <p>Loading...</p>;
  
    return (
        <div className="max-w-lg mx-auto bg-neutral-950 border-sky-500 shadow-sky-400 border p-6 rounded-2xl shadow-lg">
        <h2 className="text-2xl text-sky-500 font-semibold text-center mb-6">Update Profile</h2>
      
        <div className="mb-4">
          <label className="block text-sky-500 font-medium">Name:</label>
          <input 
            type="text" 
            value={name} 
            onChange={(e) => setName(e.target.value)} 
            className="w-full p-2 border rounded-lg focus:ring-2 focus:ring-blue-400 outline-none"
          />
        </div>
      
        <div className="mb-4">
          <label className="block text-sky-500 font-medium">Email:</label>
          <input 
            type="email" 
            value={email} 
            onChange={(e) => setEmail(e.target.value)} 
            className="w-full p-2 border rounded-lg focus:ring-2 focus:ring-blue-400 outline-none"
          />
        </div>
      
        <div className="mb-4">
          <label className="block text-sky-500 font-medium">Phone:</label>
          <input 
            type="text" 
            value={phone} 
            onChange={(e) => setPhone(e.target.value)} 
            className="w-full p-2 border rounded-lg focus:ring-2 focus:ring-blue-400 outline-none"
          />
        </div>
      
        <div className="mb-4">
          <label className="block text-sky-500 font-medium">Skills:</label>
          <div className="grid grid-cols-2 gap-2">
            {skills.map(skill => (
              <label key={skill.id} className="flex items-center space-x-2 text-sky-500">
                <input 
                  type="checkbox" 
                  value={skill.id} 
                  checked={selectedSkills.includes(skill.id)} 
                  onChange={(e) => {
                    const skillId = Number(e.target.value);
                    setSelectedSkills(prev =>
                      prev.includes(skillId) ? prev.filter(id => id !== skillId) : [...prev, skillId]
                    );
                  }} 
                  className="w-4 h-4"
                />
                <span>{skill.name}</span>
              </label>
            ))}
          </div>
        </div>

        <div>
            <button 
        onClick={infoSave} 
        className="mt-2 w-full ring-2 ring-green-500 text-white py-2 rounded-lg hover:bg-gray-700 hover:shadow-[0_0_5px_#4ADE80,0_0_10px_#4ADE80,0_0_20px_#4ADE80] transition"
        >
        Save Info
        </button>
        <button 
        onClick={() => navigate("/userfeed")} 
        className="mt-2 w-full ring-2 ring-red-500 text-white py-2 rounded-lg hover:bg-gray-700 hover:shadow-[0_0_5px_#D46606,0_0_10px_#D46606,0_0_20px_#D46606] transition"
        >
        Cancel
        </button>
        </div>
      </div>
      
    );
  };
  
  export default Register;