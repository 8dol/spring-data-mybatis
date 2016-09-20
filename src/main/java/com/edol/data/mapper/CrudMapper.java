package com.edol.data.mapper;

import java.io.Serializable;
import java.util.List;

/**
 * Created by mind on 7/17/15.
 *
 * @param <T>  需要操作的数据对象
 * @param <PK> 数据对象主键类型，一般为Integer
 */
public interface CrudMapper<T, PK extends Serializable> {

    /**
     * 根据id删除对象
     *
     * @param id
     * @return 返回影响的行数，0为删除失败。
     */
    int deleteById(PK id);

    /**
     * 插入一个新的对象
     *
     * @param record 对应的记录
     * @return 返回影响的行数，成功返回 1
     */
    int insert(T record);

    /**
     * 根据id查询对象
     *
     * @param id
     * @return 不存在则返回null
     */
    T findById(PK id);

    /**
     * 取出所有记录，大表禁用
     *
     * @return 如果表记录为空，返回空队列（list对象非null，是一个size为 0 的list）。
     */
    List<T> findAll();

    /**
     * 根据id更新对象
     *
     * @param record
     * @return 返回影响的行数，失败返回0
     */
    int updateById(T record);

    /**
     * 批量插入
     *
     * @param list
     * @return 返回影响的行数，成功返回 >= 1
     */
    int batchInsert(List<T> list);
}
