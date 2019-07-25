package com.saket.ppmtool.services;

import com.saket.ppmtool.domain.Backlog;
import com.saket.ppmtool.domain.Project;
import com.saket.ppmtool.domain.ProjectTask;
import com.saket.ppmtool.exceptions.ProjectNotFoundException;
import com.saket.ppmtool.repositories.BacklogRepository;
import com.saket.ppmtool.repositories.ProjectRepository;
import com.saket.ppmtool.repositories.ProjectTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;

@Service
public class ProjectTaskService {

    @Autowired
    private BacklogRepository backlogRepository;

    @Autowired
    private ProjectTaskRepository projectTaskRepository;

    @Autowired
    private ProjectRepository projectRepository;

    public ProjectTask addProjectTask(String projectIdentifier, ProjectTask projectTask){
        //PTs added to specific projects, project exists, BL exists
        //set the backlog to project task
        try {
            Backlog backlog = backlogRepository.findByProjectIdentifier(projectIdentifier);

            projectTask.setBacklog(backlog);

            //project Sequence
            Integer backlogSequence = backlog.getPTSequence();

            backlogSequence++;

            backlog.setPTSequence(backlogSequence);

            //adding sequence to project task
            projectTask.setProjectSequence(projectIdentifier + "-" + backlogSequence);
            projectTask.setProjectIdentifier(projectIdentifier);

            //assigning Priority
            if (projectTask.getPriority() == null) { //projectTask.getPriority() == 0
                projectTask.setPriority(3);
            }

            //assign Status
            if (projectTask.getStatus() == "" || projectTask.getStatus() == null) {
                projectTask.setStatus("TO_DO");
            }

            return projectTaskRepository.save(projectTask);
        }catch (Exception e){
            throw new ProjectNotFoundException("Project with ID: " + projectIdentifier + " Not Found");
        }

    }

    public Iterable<ProjectTask> findBacklogById(String backlog_id) {

        Project project = projectRepository.findByProjectIdentifier(backlog_id);

        if(project == null){
            throw new ProjectNotFoundException("Project with ID '"+backlog_id+"' not found");
        }

        return projectTaskRepository.findByProjectIdentifierOrderByPriority(backlog_id);
    }
}
