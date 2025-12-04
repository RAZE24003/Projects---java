package com.projecttt.projectassignment.repository;

import com.projecttt.projectassignment.model.Project;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project,Integer> {

    @Modifying
    @Transactional
    @Query(value ="DELETE FROM STUDENT_PROJECT WHERE PROJECT_ID=?", nativeQuery = true)
    public void deleteFromStudentProjectByProjectId(Integer project_id);
}
