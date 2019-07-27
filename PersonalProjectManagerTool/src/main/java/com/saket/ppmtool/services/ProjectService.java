package com.saket.ppmtool.services;

import com.saket.ppmtool.domain.Backlog;
import com.saket.ppmtool.domain.Project;
import com.saket.ppmtool.domain.User;
import com.saket.ppmtool.exceptions.ProjectIdException;
import com.saket.ppmtool.repositories.BacklogRepository;
import com.saket.ppmtool.repositories.ProjectRepository;
import com.saket.ppmtool.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private BacklogRepository backlogRepository;

    @Autowired
    private UserRepository userRepository;

    public Project saveOrUpdateProject(Project project, String username){

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

    public Project findProjectByIdentifier(String projectId){

        Project project = projectRepository.findByProjectIdentifier(projectId.toUpperCase());

        if(project == null)
            throw new ProjectIdException("Project ID '"+projectId+"' doesn't exist");

        return project;
    }

    public Iterable<Project> findAllProjects(){
        return projectRepository.findAll();
    }

    public void deleteProjectByIdentifier(String projectId){
        Project project = projectRepository.findByProjectIdentifier(projectId.toUpperCase());

        if(project == null)
            throw new ProjectIdException("Project with ID:'"+projectId+"' does't exist");

        projectRepository.delete(project);
    }
}
