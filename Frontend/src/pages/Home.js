import { useBlockchain } from "../contexts/BlockchainContext";
import { useNavigate } from "react-router-dom";
import { useEffect ,useState } from "react";
import axios from "axios";

export default function Home() {
  const { connectWallet, account } = useBlockchain();
  const navigate = useNavigate();
  const [loggedIn, setLoggedIn] = useState(false);

  useEffect(() => {
    if (account && loggedIn) {
      const data = {wallet : account,loggedIn : true};
      navigate("/userfeed",{state : data}); // Redirect to projects page if wallet is connected
    }
  }, [account, navigate]);

  const handleLogin = () => {
    axios.post("http://localhost:8080/api/user/login", { wallet: account}, { withCredentials: true })
      .then(res => {
        setLoggedIn(true);
        const name = res.data.name;
        if(name){
          navigate("/userfeed",{state:res.data});
        }
        else{
          navigate("/register",{state: account});
        }
        
      })
      .catch(() => alert("Credential error"));
  };

  return (
    <div className="flex flex-col items-center justify-center h-screen bg-neutral-950 text-sky-400">
      <h1 className="text-4xl font-bold mb-6 " >Welcome to SkillsConnect</h1>
      <p className="mb-6 text-lg">A decentralized freelance marketplace</p>

      {!account ? (
        <button
          onClick={connectWallet}
          className="my-2 ring-2 ring-blue-600  shadow-[0_0_5px_#06B6D4,0_0_10px_#06B6D4,0_0_20px_#06B6D4] insert-ring-2 insert-ring-blue-400 hover:bg-gray-700 text-white font-bold py-2 px-6 rounded-lg text-lg transition"
        >
          Connect Wallet
        </button>
      ) : (
        <p className="text-green-400 my-4">Wallet Connected: {account}</p>
      )}

      <button
        onClick={handleLogin}
        className="mt-4 ring-2 ring-green-600 shadow-[0_0_5px_#4ADE80,0_0_10px_#4ADE80,0_0_20px_#4ADE80] hover:bg-gray-700 text-white font-bold py-2 px-6 rounded-lg text-lg transition"
      >
        Login
      </button>
    </div>
  );
}
