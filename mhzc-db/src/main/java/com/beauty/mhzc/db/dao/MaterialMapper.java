package com.beauty.mhzc.db.dao;

import com.beauty.mhzc.db.domain.Material;
import com.beauty.mhzc.db.domain.MaterialExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface MaterialMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table material
     *
     * @mbg.generated
     */
    long countByExample(MaterialExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table material
     *
     * @mbg.generated
     */
    int deleteByExample(MaterialExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table material
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(String id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table material
     *
     * @mbg.generated
     */
    int insert(Material record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table material
     *
     * @mbg.generated
     */
    int insertSelective(Material record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table material
     *
     * @mbg.generated
     */
    Material selectOneByExample(MaterialExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table material
     *
     * @mbg.generated
     */
    Material selectOneByExampleSelective(@Param("example") MaterialExample example, @Param("selective") Material.Column ... selective);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table material
     *
     * @mbg.generated
     */
    List<Material> selectByExampleSelective(@Param("example") MaterialExample example, @Param("selective") Material.Column ... selective);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table material
     *
     * @mbg.generated
     */
    List<Material> selectByExample(MaterialExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table material
     *
     * @mbg.generated
     */
    Material selectByPrimaryKeySelective(@Param("id") String id, @Param("selective") Material.Column ... selective);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table material
     *
     * @mbg.generated
     */
    Material selectByPrimaryKey(String id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table material
     *
     * @mbg.generated
     */
    Material selectByPrimaryKeyWithLogicalDelete(@Param("id") String id, @Param("andLogicalDeleted") boolean andLogicalDeleted);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table material
     *
     * @mbg.generated
     */
    int updateByExampleSelective(@Param("record") Material record, @Param("example") MaterialExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table material
     *
     * @mbg.generated
     */
    int updateByExample(@Param("record") Material record, @Param("example") MaterialExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table material
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(Material record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table material
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(Material record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table material
     *
     * @mbg.generated
     */
    int logicalDeleteByExample(@Param("example") MaterialExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table material
     *
     * @mbg.generated
     */
    int logicalDeleteByPrimaryKey(String id);
}