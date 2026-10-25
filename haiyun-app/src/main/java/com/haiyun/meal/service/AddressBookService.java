package com.haiyun.meal.service;

import com.haiyun.meal.entity.AddressBook;
import java.util.List;

/**
 * 地址簿业务接口。
 */
public interface AddressBookService {

    /**
     * 查询地址簿列表。
     */
    List<AddressBook> list(AddressBook addressBook);

    /**
     * 新增地址簿。
     */
    void save(AddressBook addressBook);

    /**
     * 根据主键查询地址簿。
     */
    AddressBook getById(Long id);

    /**
     * 修改地址簿。
     */
    void update(AddressBook addressBook);

    /**
     * 设置默认地址。
     */
    void setDefault(AddressBook addressBook);

    /**
     * 删除地址。
     */
    void deleteById(Long id);

}
