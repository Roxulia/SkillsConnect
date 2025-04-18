package com.skillsconnect.backend.services;

import com.skillsconnect.backend.models.Skill;
import com.skillsconnect.backend.models.User;
import com.skillsconnect.backend.repositories.SkillRepository;
import com.skillsconnect.backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SkillRepository skillRepository;

    @Autowired
    private FileUploadService fileUploadService;

    public User userLogin(String wallet)
    {
        Optional<User> users = userRepository.findByWallet(wallet);
        if(users.isPresent())
        {
            return users.get();
        }
        User user = new User(wallet);
        return userRepository.save(user);
    }

    public File getUserProfile(Long id)
    {
        User u = getUserById(id);
        String url = u.getProfile_img();
        return fileUploadService.getProfile(url);
    }

    public User getUserById(Long id)
    {
        return userRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("User not found with ID : " + id));
    }

    public void updateName(Long id,String name)
    {
        userRepository.updateName(id,name);
    }

    public void updateEmail(Long id,String mail)
    {
        userRepository.updateEmail(id,mail);
    }

    public void updatePhone(Long id,String ph)
    {
        userRepository.updatePh(id,ph);
    }

    public void updateProfileImage(Long id,String url)
    {
        userRepository.updateProfileImage(id,url);
    }

    public User updateUser(Long id,String name,String email,String ph)
    {
        User u = getUserById(id);
        u.setName(name);
        u.setEmail(email);
        u.setPh_number(ph);
        return userRepository.save(u);
    }

    public void updateSkills(Long id,List<Long> skills)
    {
        List<Skill> skillList = skillRepository.findAllById(skills);
        Set<Skill> skillSet = new HashSet<>(skillList);
        User u = getUserById(id);
        u.setSkills(skillSet);
        userRepository.save(u);
    }

    public Page<User> getAllUser(Pageable numberOfUseronPage)
    {
        return userRepository.findAll(numberOfUseronPage);
    }

}
