package me.x.impls.services;

import cn.edu.sustech.cs307.database.SQLDataSource;
import cn.edu.sustech.cs307.dto.Major;
import cn.edu.sustech.cs307.exception.EntityNotFoundException;
import cn.edu.sustech.cs307.service.MajorService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MyMajorService implements MajorService {

    @Override
    public int addMajor(String name, int departmentId) {
        try (Connection connection = SQLDataSource.getInstance().getSQLConnection();
             PreparedStatement addMajor = connection.prepareStatement("insert into \"Majors\" (name, \"departmentId\") values (?,?);");
             PreparedStatement getId = connection.prepareStatement(
                     "select id from  \"Majors\" where name = (?);"
             )
        ) {
            addMajor.setString(1,name);
            addMajor.setInt(2,departmentId);
            addMajor.execute();
            getId.setString(1,name);
            ResultSet resultSet = getId.executeQuery();
            resultSet.next();
            return resultSet.getInt(1);
        } catch (SQLException e) {
            throw new EntityNotFoundException();
        }
    }


    /**
     * To remove an entity from the system, related entities dependent on this entity
     * (usually rows referencing the row to remove through foreign keys in a relational database) shall be removed together.
     *
     * More specifically, when remove a major, the related students should be removed accordingly
     * @param majorId
     */
    @Override
    public void removeMajor(int majorId) {
        try (Connection connection = SQLDataSource.getInstance().getSQLConnection();
             PreparedStatement first_query = connection.prepareStatement("delete from \"Majors\" where id = (?)");
        ) {
            first_query.setInt(1,majorId);
            first_query.execute();
        } catch (SQLException e) {
            throw new EntityNotFoundException();
        }
    }


    @Override
    public List<Major> getAllMajors() {
        ArrayList<Major> majors = new ArrayList<>();
        try (Connection connection = SQLDataSource.getInstance().getSQLConnection();
             PreparedStatement first_query = connection.prepareStatement("select * from \"Majors\"");
        ) {
            ResultSet resultSet = first_query.executeQuery();
            while (resultSet.next()){
                Major major = new Major();
                major.id = resultSet.getInt(1);
                major.name = resultSet.getString(2);
                major.department = new MyDepartmentService().getDepartment(resultSet.getInt(3));
                majors.add(major);
            }
            return majors;
        } catch (SQLException e) {
            return majors;
        }
    }


    /**
     * If there is no Major about specific id, throw EntityNotFoundException.
     * @param majorId
     * @return
     */
    @Override
    public Major getMajor(int majorId) {
        try (Connection connection = SQLDataSource.getInstance().getSQLConnection();
             PreparedStatement first_query = connection.prepareStatement("select * from \"Majors\" where id = ?");
        ) {
            first_query.setInt(1,majorId);
            ResultSet resultSet = first_query.executeQuery();

            if (resultSet.next()){
                Major major = new Major();
                major.id = majorId;
                major.name = resultSet.getString(2);
                major.department = new MyDepartmentService().getDepartment(resultSet.getInt(3));
                return major;
            }else {
                throw new EntityNotFoundException();
            }
        } catch (SQLException e) {
            throw new EntityNotFoundException();
        }
    }

    /**
     * Binding a course id {@code courseId} to major id {@code majorId}, and the selection is compulsory.
     * @param majorId the id of major
     * @param courseId the course id
     */
    @Override
    public void addMajorCompulsoryCourse(int majorId, String courseId) {
        try (Connection connection = SQLDataSource.getInstance().getSQLConnection();
             PreparedStatement second_query = connection.prepareStatement("insert into \"majorCourses\" (\"majorId\", \"courseId\", selection) values (?, ?, ?)")
        ) {
            second_query.setInt(1,majorId);
            second_query.setString(2,courseId);
            second_query.setString(3,"COMPULSORY");
            second_query.execute();
        } catch (SQLException e) {
            throw new EntityNotFoundException();
        }
    }


    /**
     * Binding a course id{@code courseId} to major id {@code majorId}, and the selection is elective.
     * @param majorId the id of major
     * @param courseId the course id
     */
    @Override
    public void addMajorElectiveCourse(int majorId, String courseId) {
        try (Connection connection = SQLDataSource.getInstance().getSQLConnection();
             PreparedStatement second_query = connection.prepareStatement("insert into \"majorCourses\" (\"majorId\", \"courseId\", selection) values (?, ?, ?)")
        ) {
            second_query.setInt(1,majorId);
            second_query.setString(2,courseId);
            second_query.setString(3,"ELECTIVE");
            second_query.execute();
        } catch (SQLException e) {
            throw new EntityNotFoundException();
        }
    }
}
