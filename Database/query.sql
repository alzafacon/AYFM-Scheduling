final static String SUGGESTION =  
        "SELECT " +
          + "p.*, " 
          + "a.*"
        +  "FROM " 
          + "ayfm.person AS p "
            + "LEFT JOIN " 
          + "ayfm.assignment AS a "
            + "ON p.id = a.assignee " 
            + "OR p.id = a.householder " 
          + "WHERE " 
            + "TRUE = p.isactive " 
          + "GROUP BY p.id " 
          + "ORDER BY MAX(a.week);";
    
