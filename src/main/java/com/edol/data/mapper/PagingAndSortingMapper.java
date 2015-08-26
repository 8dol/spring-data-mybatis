package com.edol.data.mapper;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.Serializable;
import java.util.List;

/**
 * Created by mind on 8/26/15.
 */
public interface PagingAndSortingMapper<T, PK extends Serializable> extends CrudMapper<T, PK> {

    /**
     * Returns all entities sorted by the given options.
     *
     * @param sort
     * @return all entities sorted by the given options
     */
    List<T> findAll(Sort sort);

    /**
     * Returns a list of entities meeting the paging restriction provided in the {@code Pageable} object.
     *
     * @param pageable
     * @return a page of entities
     */
    List<T> findAll(Pageable pageable);
}
