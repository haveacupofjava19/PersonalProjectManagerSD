package com.saket.ppmtool.services;

import com.saket.ppmtool.domain.Backlog;
import com.saket.ppmtool.domain.ProjectTask;
import com.saket.ppmtool.repositories.BacklogRepository;
import com.saket.ppmtool.repositories.ProjectTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProjectTaskService {

    @Autowired
    private BacklogRepository backlogRepository;

    @Autowired
    private ProjectTaskRepository projectTaskRepository;

    public ProjectTask addProjectTask(String projectIdentifier, ProjectTask projectTask){
        //PTs added to specific projects, project exists, BL exists
        //set the backlog to project task

        Backlog backlog = backlogRepository.findByProjectIdentifier(projectIdentifier);

        projectTask.setBacklog(backlog);

        //project Sequence
        Integer backlogSequence = backlog.getPTSequence();

        backlogSequence++;

        //adding sequence to project task
        projectTask.setProjectSequence(projectIdentifier+"-"+backlogSequence);
        projectTask.setProjectIdentifier(projectIdentifier);

        //assigning Priority
//        if(projectTask.getPriority() == 0 || projectTask.getPriority() == null){
//            projectTask.setPriority(3);
//        }

        //assign Status
        if(projectTask.getStatus() == "" || projectTask.getStatus() == null){
            projectTask.setStatus("TO_DO");
        }

        return projectTaskRepository.save(projectTask);

    }

}
