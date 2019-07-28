package com.saket.ppmtool.services;

import com.saket.ppmtool.domain.Backlog;
import com.saket.ppmtool.domain.Project;
import com.saket.ppmtool.domain.User;
import com.saket.ppmtool.exceptions.ProjectIdException;
import com.saket.ppmtool.exceptions.ProjectNotFoundException;
import com.saket.ppmtool.repositories.BacklogRepository;
import com.saket.ppmtool.repositories.ProjectRepository;
import com.saket.ppmtool.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private BacklogRepository backlogRepository;

    @Autowired
    private UserRepository userRepository;

    public Project saveOrUpdateProject(Project project, String username){

        if(project.getId() != null){
            Project exisitingProject = projectRepository.findByProjectIdentifier(project.getProjectIdentifier());

            if(exisitingProject != null && (!exisitingProject.getProjectLeader().equals(username))){
                throw new ProjectNotFoundException("Project Not found for user: "+username);
            }else if(exisitingProject == null){
                throw new ProjectNotFoundException("Project with ID: '"+project.getProjectIdentifier()+"' cannot be updated as it doesn't exist");
            }
        }


        try{

            User user = userRepository.findByUsername(username);

            project.setUser(user);
            project.setProjectLeader(user.getUsername());

            String projectIdentifierAs = project.getProjectIdentifier().toUpperCase();

            project.setProjectIdentifier(projectIdentifierAs);

            if(project.getId() == null) {
                Backlog backlog = new Backlog();
                project.setBacklog(backlog);
                backlog.setProject(project);
                backlog.setProjectIdentifier(projectIdentifierAs);
            }

            if(project.getId() != null){
                project.setBacklog(backlogRepository.findByProjectIdentifier(projectIdentifierAs));
            }

            return projectRepository.save(project);
        }catch (Exception e){
            throw new ProjectIdException("Project ID '"+project.getProjectIdentifier().toUpperCase()+"' already exists");
        }
    }

    public Project findProjectByIdentifier(String projectId, String username){

        Project project = projectRepository.findByProjectIdentifier(projectId.toUpperCase());

        if(project == null)
            throw new ProjectIdException("Project ID '"+projectId+"' doesn't exist");

        if(!project.getProjectLeader().equals(username))
            throw new ProjectNotFoundException("Project not found for user: '"+username+"'");

        return project;
    }

    public Iterable<Project> findAllProjects(String username){
        return projectRepository.findAllByProjectLeader(username);
    }

    public void deleteProjectByIdentifier(String projectId, String username){


        projectRepository.delete(findProjectByIdentifier(projectId, username));
    }
}
