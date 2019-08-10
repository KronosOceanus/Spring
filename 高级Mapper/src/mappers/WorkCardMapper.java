package mappers;

import entity.EmployeeTask;
import entity.WorkCard;

public interface WorkCardMapper {

    WorkCard getWorkCardByEmpId(int empId);

}
