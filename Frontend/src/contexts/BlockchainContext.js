import { createContext, useContext, useEffect, useState } from "react";
import { BrowserProvider, Contract, parseEther } from "ethers";
import contractABI from "../ABI.json";

const BlockchainContext = createContext();

const CONTRACT_ADDRESS = "0xF7679259A6dBD58fadb6eEdF8ce979b71752864a";

export const BlockchainProvider = ({ children }) => {
  const [provider, setProvider] = useState(null);
  const [signer, setSigner] = useState(null);
  const [contract, setContract] = useState(null);
  const [projects, setProjects] = useState([]);
  const [account, setAccount] = useState(null);


  const connectWallet = async () => {
    if (window.ethereum) {
      try {
        const provider = new BrowserProvider(window.ethereum);
        setProvider(provider);
        const signer = await provider.getSigner();
        setSigner(signer);
        const contract = new Contract(CONTRACT_ADDRESS, contractABI, signer);
        setContract(contract);
        const accounts = await provider.send("eth_requestAccounts", []);
        setAccount(accounts[0]);
        loadProjects(contract);
      } catch (error) {
        console.error("Wallet connection failed", error);
      }
    } else {
      alert("Please install MetaMask to use this application.");
    }
  };

  const loadProjects = async (contract) => {
    const projectsData = [];
    let i = 0;
    while (true) {
      try {
        const project = await contract.projects(i);
        projectsData.push(project);
        i++;
      } catch (error) {
        break;
      }
    }
    setProjects(projectsData);
  };

  const createProject = async (name, duration) => {
    const tx = await contract.createProject(name, duration);
    await tx.wait();
    loadProjects(contract);
  };

  const acceptBid = async (id, amount, bidder) => {
    const tx = await contract.acceptBid(id, parseEther(amount), bidder, { value: parseEther(amount) });
    await tx.wait();
    loadProjects(contract);
  };

  const finishProject = async (id) => {
    const tx = await contract.finishProject(id);
    await tx.wait();
    loadProjects(contract);
  };

  const refundProject = async (id) => {
    const tx = await contract.refundProject(id);
    await tx.wait();
    loadProjects(contract);
  };

  const setUploaded = async (id, location) => {
    const tx = await contract.setUploaded(id, location);
    await tx.wait();
    loadProjects(contract);
  };

  const disconnectWallet = async () => {
    setAccount(null);
    setSigner(null);
    setContract(null);
    localStorage.removeItem("walletAddress"); // Optional: Clear any cached wallet
  };
  

  return (
    <BlockchainContext.Provider
      value={{
        provider,
        signer,
        contract,
        projects,
        account,
        disconnectWallet,
        connectWallet,
        createProject,
        acceptBid,
        finishProject,
        refundProject,
        setUploaded,
        loadProjects,
      }}
    >
      {children}
    </BlockchainContext.Provider>
  );
};

// Hook to use the blockchain context in components
export const useBlockchain = () => {
  return useContext(BlockchainContext);
};
