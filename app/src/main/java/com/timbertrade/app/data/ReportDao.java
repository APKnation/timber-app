package com.timbertrade.app.data;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;
import java.util.List;
import com.timbertrade.app.models.Report;

@Dao
public interface ReportDao {
    @Query("SELECT * FROM reports")
    List<Report> getAll();
    @Insert
    void insert(Report report);
    @Update
    void update(Report report);
    @Delete
    void delete(Report report);
    @Query("DELETE FROM reports WHERE id = :id")
    void deleteById(String id);
}
