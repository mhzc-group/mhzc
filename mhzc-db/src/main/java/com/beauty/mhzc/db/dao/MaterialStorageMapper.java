package com.beauty.mhzc.db.dao;

import com.beauty.mhzc.db.domain.MaterialStorage;
import com.beauty.mhzc.db.domain.MaterialStorageExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface MaterialStorageMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table material_storage
     *
     * @mbg.generated
     */
    long countByExample(MaterialStorageExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table material_storage
     *
     * @mbg.generated
     */
    int deleteByExample(MaterialStorageExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table material_storage
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(String id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table material_storage
     *
     * @mbg.generated
     */
    int insert(MaterialStorage record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table material_storage
     *
     * @mbg.generated
     */
    int insertSelective(MaterialStorage record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table material_storage
     *
     * @mbg.generated
     */
    MaterialStorage selectOneByExample(MaterialStorageExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table material_storage
     *
     * @mbg.generated
     */
    MaterialStorage selectOneByExampleSelective(@Param("example") MaterialStorageExample example, @Param("selective") MaterialStorage.Column ... selective);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table material_storage
     *
     * @mbg.generated
     */
    List<MaterialStorage> selectByExampleSelective(@Param("example") MaterialStorageExample example, @Param("selective") MaterialStorage.Column ... selective);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table material_storage
     *
     * @mbg.generated
     */
    List<MaterialStorage> selectByExample(MaterialStorageExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table material_storage
     *
     * @mbg.generated
     */
    MaterialStorage selectByPrimaryKeySelective(@Param("id") String id, @Param("selective") MaterialStorage.Column ... selective);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table material_storage
     *
     * @mbg.generated
     */
    MaterialStorage selectByPrimaryKey(String id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table material_storage
     *
     * @mbg.generated
     */
    int updateByExampleSelective(@Param("record") MaterialStorage record, @Param("example") MaterialStorageExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table material_storage
     *
     * @mbg.generated
     */
    int updateByExample(@Param("record") MaterialStorage record, @Param("example") MaterialStorageExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table material_storage
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(MaterialStorage record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table material_storage
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(MaterialStorage record);
}