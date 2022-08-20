SELECT IFNULL(
  SELECT DISTINCT salary from employee limit 1 offset 1 order by salary desc
, NULL ) AS SecondHighestSalary