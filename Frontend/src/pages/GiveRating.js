import { useBlockchain } from "../contexts/BlockchainContext";
import { useNavigate,useLocation } from "react-router-dom";
import { useEffect ,useState } from "react";
import axios from "axios";
function GiveRatingPage()
{
    const location = useLocation();
    const data = location.state;
    const navigate = useNavigate();
    const {account} = useBlockchain();
    const [user,setUser] = useState(null);
    const [viewer,setViewer] = useState(null);

    useEffect(()=>{
        if(!account || !data)
        {
            navigate("/");
        }

        axios.get("http://localhost:8080/api/user/me", { withCredentials: true })
                .then(response => {
                    console.log(response.data);
                setViewer(response.data);
                })
                .catch(error => console.error("Error fetching user data:", error));

        axios.get(`http://localhost:8080/api/user/${data.uid}`, {
            withCredentials: true,
          })
          .then((res) => {
            //console.log(res.data);    
            setUser(res.data);
          } )
          .catch(error => alert(`Error fetching project: ` + error.response.data));
    },[account,navigate,data]);

    return (<div>hehe</div>)
}

export default GiveRatingPage;