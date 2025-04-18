package com.skillsconnect.backend.services;

import com.skillsconnect.backend.DTO.SkillDTO;
import com.skillsconnect.backend.models.Skill;
import com.skillsconnect.backend.models.User;
import com.skillsconnect.backend.repositories.SkillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SkillService {

    @Autowired
    private SkillRepository skillRepository;

    public List<Skill> getAllSkills(){
        return skillRepository.findAll();
    }

    public List<SkillDTO> getallSkillDTOs()
    {
        List<Skill> skillList = getAllSkills();
        List<SkillDTO> skillDTOS = new ArrayList<>();
        for(Skill s: skillList)
        {
            SkillDTO skillDTO = new SkillDTO(s.getId(),s.getName());
            skillDTOS.add(skillDTO);
        }
        return skillDTOS;
    }

    public List<String> getAllSkillsName(){return skillRepository.getAllSkillName();}

    public Skill addNewSkill(String name){
        Skill s = new Skill();
        s.setName(name);
        return skillRepository.save(s);
    }

    public List<String> getUserSkills(Long id)
    {
        return skillRepository.getSkillsByUserId(id);
    }

    public List<User> getUserWithSkill(Long id)
    {
        return skillRepository.getUsersBySkillId(id);
    }

    public Skill getSkillById(Long id)
    {
        return skillRepository.findById(id)
                .orElseThrow(()->new RuntimeException("Cant Find Skill with ID : "+id));
    }
}
