package com.paypay.baymax.core.dao.catalog;

import org.springframework.stereotype.Repository;
import com.paypay.baymax.core.dao.GenericDAOImpl;
import com.paypay.baymax.domain.employee.TEmployee;

@Repository
public class EmployeeDAO extends GenericDAOImpl<TEmployee, Long> implements IEmployeeDAO {

}
