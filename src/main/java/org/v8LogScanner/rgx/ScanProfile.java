package org.v8LogScanner.rgx;

import org.v8LogScanner.logs.LogsOperations;
import org.v8LogScanner.rgx.RegExp.PropTypes;

import java.io.Serializable;
import java.util.List;

public interface ScanProfile extends Serializable, Cloneable {

    enum RgxOpTypes {CURSOR_OP, HEAP_OP, USER_OP}

    enum GroupTypes {BY_PROPS, BY_FILE_NAMES}

    enum LogTypes {RPHOST, CLIENT, RAGENT, RMNGR, ANY}

    enum DateRanges {ANY, THIS_HOUR, LAST_HOUR, TODAY, YESTERDAY, THIS_WEEK, THIS_MONTH, PREV_WEEK, PREV_MONTH, SET_OWN}

    int getId();

    void setId(int id);

    String getName();

    void setName(String name);

    List<String> getLogPaths();

    void addLogPath(String logPath);

    void setLogPaths(List<String> logPaths);

    DateRanges getDateRange();

    void setDateRange(DateRanges _dateRange);

    void setStartDate(String startDate);

    String getStartDate();

    void setEndDate(String startDate);

    String getEndDate();

    int getLimit();

    void setLimit(int limit);

    void setRgxOp(RgxOpTypes rgxOp);

    RgxOpTypes getRgxOp();

    LogTypes getLogType();

    void setLogType(LogTypes _logType);

    PropTypes getSortingProp();

    void setSortingProp(PropTypes sortingProp);

    GroupTypes getGroupType();

    void setGroupType(GroupTypes userGroupType);

    List<RegExp> getRgxList();

    void setRgxList(List<RegExp> rgxList);

    void addRegExp(RegExp rgx);

    String getRgxExp();

    void setRgxExp(String rgx);

    ScanProfile clone();

    void clear();

    ////////////////////
    // MANAGER FUNCTIONS
    ////////////////////

    static void buildTopSlowestSql(ScanProfile profile) {

        profile.clear();
        profile.setRgxOp(RgxOpTypes.CURSOR_OP);
        profile.setLogType(LogTypes.RPHOST);

        List<RegExp> rgxList = profile.getRgxList();
        RegExp dbmsql = new RegExp(RegExp.EventTypes.DBMSSQL);
        dbmsql.getGroupingProps().add(PropTypes.Event);
        dbmsql.getGroupingProps().add(PropTypes.Context);
        dbmsql.getFilter(PropTypes.Context).add(""); // only seek events with existing prop
        rgxList.add(dbmsql);

        RegExp sdbl = new RegExp(RegExp.EventTypes.SDBL);
        sdbl.getGroupingProps().add(PropTypes.Event);
        sdbl.getGroupingProps().add(PropTypes.Context);
        sdbl.getFilter(PropTypes.Context).add(""); // only seek events with present prop
        rgxList.add(sdbl);

        RegExp dbv8d8eng = new RegExp(RegExp.EventTypes.DBV8DBEng);
        dbv8d8eng.getGroupingProps().add(PropTypes.Event);
        dbv8d8eng.getGroupingProps().add(PropTypes.Context);
        dbv8d8eng.getFilter(PropTypes.Context).add(""); // only seek events with present prop
        rgxList.add(dbv8d8eng);

        profile.setName("Top slowest SQL and SDBL events");

        profile.setSortingProp(PropTypes.Duration);
    }

    static void buildTopSlowestSqlByUser(ScanProfile profile, String userName) {

        profile.clear();
        profile.setRgxOp(RgxOpTypes.CURSOR_OP);
        profile.setLogType(LogTypes.RPHOST);

        List<RegExp> rgxList = profile.getRgxList();
        RegExp dbmsql = new RegExp(RegExp.EventTypes.DBMSSQL);
        dbmsql.getGroupingProps().add(PropTypes.Context);
        dbmsql.getFilter(PropTypes.Context).add(""); // only seek events with existing prop
        dbmsql.getFilter(PropTypes.Usr).add(userName);
        rgxList.add(dbmsql);

        RegExp sdbl = new RegExp(RegExp.EventTypes.SDBL);
        sdbl.getGroupingProps().add(PropTypes.Context);
        sdbl.getFilter(PropTypes.Context).add(""); // only seek events with present prop
        sdbl.getFilter(PropTypes.Usr).add(userName);
        rgxList.add(sdbl);

        RegExp dbv8d8eng = new RegExp(RegExp.EventTypes.DBV8DBEng);
        dbv8d8eng.getGroupingProps().add(PropTypes.Context);
        dbv8d8eng.getFilter(PropTypes.Context).add(""); // only seek events with present prop
        dbv8d8eng.getFilter(PropTypes.Usr).add(userName);
        rgxList.add(dbv8d8eng);

        profile.setSortingProp(PropTypes.Duration);
        profile.setName("Top slowest SQL and SDBL events by USER: " + userName);
    }

    static void buildRphostExcp(ScanProfile profile) {

        profile.clear();
        profile.setRgxOp(RgxOpTypes.HEAP_OP);

        RegExp excp = new RegExp(RegExp.EventTypes.EXCP);
        excp.getGroupingProps().add(PropTypes.Descr);

        profile.getRgxList().add(excp);
        profile.setLogType(LogTypes.RPHOST);
        profile.setName("EXCP events from RPHOST");
    }

    static void buildUserEXCP(ScanProfile profile) {
        profile.clear();
        profile.setRgxOp(RgxOpTypes.HEAP_OP);

        RegExp excp = new RegExp(RegExp.EventTypes.EXCP);
        excp.getFilter(PropTypes.Usr).add(""); // seek events with present props
        excp.getGroupingProps().add(PropTypes.Usr);
        profile.getRgxList().add(excp);
        profile.setName("EXCP events caused by users");
    }

    static void buildSqlDeadlLockError(ScanProfile profile) {
        profile.clear();
        profile.setRgxOp(RgxOpTypes.CURSOR_OP);
        profile.setLogType(LogTypes.RPHOST);
        profile.setGroupType(GroupTypes.BY_PROPS);

        List<RegExp> rgxList = profile.getRgxList();

        List<String> sqlTexts = org.v8LogScanner.logsCfg.LogConfig.sql_lock_texts();

        sqlTexts.forEach((sqlMsg) -> {
            RegExp excp = new RegExp(RegExp.EventTypes.EXCP);
            excp.getGroupingProps().add(PropTypes.Descr);
            excp.getFilter(PropTypes.Descr).add(sqlMsg);
            rgxList.add(excp);
        });
        profile.setName("DEADLOCKS and TIMEOUTS occurred on database level");
    }

    static void build1cDeadlocksError(ScanProfile profile) {

        profile.clear();
        profile.setRgxOp(RgxOpTypes.HEAP_OP);
        profile.setLogType(LogTypes.RPHOST);

        List<RegExp> rgxList = profile.getRgxList();

        RegExp.EventTypes[] v8Locks = {RegExp.EventTypes.TTIMEOUT, RegExp.EventTypes.TDEADLOCK};

        for (RegExp.EventTypes eventType : v8Locks) {
            RegExp rgxEvent = new RegExp(eventType);
            rgxEvent.getGroupingProps().add(PropTypes.Event);
            rgxEvent.getGroupingProps().add(PropTypes.Context);
            rgxEvent.getFilter(PropTypes.Context).add(""); // only seek events with existing prop
            rgxList.add(rgxEvent);
        }
        profile.setName("DEADLOCKS and TIMEOUTS occurred on 1C level");
    }

    static void build1cDeadlocksByConnectID(ScanProfile profile, String connectID) {

        profile.clear();
        profile.setRgxOp(RgxOpTypes.HEAP_OP);
        profile.setLogType(LogTypes.RPHOST);

        List<RegExp> rgxList = profile.getRgxList();

        RegExp.EventTypes[] v8Locks = {RegExp.EventTypes.TLOCK, RegExp.EventTypes.TDEADLOCK};

        for (RegExp.EventTypes eventType : v8Locks) {
            RegExp rgxEvent = new RegExp(eventType);
            rgxEvent.getGroupingProps().add(PropTypes.ConnectID);
            rgxEvent.getFilter(PropTypes.Context).add(""); // only seek events with existing prop
            rgxEvent.getFilter(PropTypes.ConnectID).add(connectID);
            if (eventType == RegExp.EventTypes.TDEADLOCK)
                rgxEvent.getFilter(PropTypes.DeadlockConnectionIntersections).add(connectID);
            rgxList.add(rgxEvent);
        }
        profile.setName("1C DEADLOKS and TIMEOUTS filtered by CONNECT_ID: " + connectID);
    }

    static void buildAllLocksByUser(ScanProfile profile, String user) {
        profile.clear();
        profile.setRgxOp(RgxOpTypes.CURSOR_OP);
        profile.setLogType(LogTypes.RPHOST);

        List<RegExp> rgxList = profile.getRgxList();

        RegExp.EventTypes[] v8Locks = {RegExp.EventTypes.TTIMEOUT, RegExp.EventTypes.TDEADLOCK};

        for (RegExp.EventTypes eventType : v8Locks) {
            RegExp rgxEvent = new RegExp(eventType);
            rgxEvent.getGroupingProps().add(PropTypes.Event);
            rgxEvent.getGroupingProps().add(PropTypes.Context);
            rgxEvent.getFilter(PropTypes.Context).add(""); // only seek events with existing prop
            rgxEvent.getFilter(PropTypes.Usr).add(user);
            rgxList.add(rgxEvent);
        }

        List<String> sqlTexts = org.v8LogScanner.logsCfg.LogConfig.sql_lock_texts();
        sqlTexts.forEach((sqlMsg) -> {
            RegExp excp = new RegExp(RegExp.EventTypes.EXCP);
            excp.getGroupingProps().add(PropTypes.Descr);
            excp.getFilter(PropTypes.Descr).add(sqlMsg);
            excp.getFilter(PropTypes.Usr).add(user);
            rgxList.add(excp);
        });
        profile.setName("SQL and 1C locks filtered by USER: " + user);
    }

    static void buildAllUsersEvents(ScanProfile profile) {
        profile.clear();
        profile.setRgxOp(RgxOpTypes.HEAP_OP);

        RegExp event = new RegExp(RegExp.EventTypes.ANY);
        event.getFilter(PropTypes.Usr).add(""); // seek events with present props
        event.getGroupingProps().add(PropTypes.Usr);
        profile.getRgxList().add(event);
        profile.setName("ALL USERS EVENTS");
    }

    static void buildSlowestEventsByUser(ScanProfile profile, String userName) {
        profile.clear();
        profile.setRgxOp(RgxOpTypes.CURSOR_OP);

        RegExp event = new RegExp(RegExp.EventTypes.ANY);
        event.getFilter(PropTypes.Usr).add(userName); // seek events with present props
        event.getGroupingProps().add(PropTypes.Usr);
        event.getGroupingProps().add(PropTypes.Event);
        profile.getRgxList().add(event);

        profile.setSortingProp(PropTypes.Duration);
        profile.setName("SLOWEST events by user: " + userName);
    }

    // for investigating locks escalation
    static void buildFindSQlEventByQueryFragment(ScanProfile profile, String query_fragment) {
        profile.clear();
        profile.setRgxOp(RgxOpTypes.HEAP_OP);
        profile.setLogType(LogTypes.RPHOST);
        profile.setGroupType(GroupTypes.BY_PROPS);

        List<RegExp> rgxList = profile.getRgxList();
        RegExp rgx = new RegExp(RegExp.EventTypes.DBMSSQL);
        rgx.getGroupingProps().add(PropTypes.Event);
        rgx.getGroupingProps().add(PropTypes.Sql);
        rgx.getFilter(PropTypes.Sql).add(query_fragment);
        rgxList.add(rgx);

        profile.setName("DBMSQL events filtered by sql fragment: " + query_fragment);
    }

    static void BuildFindSlowSQlEventsWithPlan(ScanProfile profile, String queryPlan) {
        profile.clear();
        profile.setRgxOp(RgxOpTypes.CURSOR_OP);
        profile.setLogType(LogTypes.RPHOST);
        profile.setGroupType(GroupTypes.BY_PROPS);

        List<RegExp> rgxList = profile.getRgxList();
        RegExp rgx = new RegExp(RegExp.EventTypes.DBMSSQL);
        rgx.getGroupingProps().add(PropTypes.Event);
        rgx.getGroupingProps().add(PropTypes.Context);
        rgx.getFilter(PropTypes.PlanSQLText).add(queryPlan);
        rgxList.add(rgx);

        profile.setSortingProp(PropTypes.Duration);
        profile.setName("SLOW sql filtered by query plan: " + queryPlan);
    }

    static void buildTopThisHourEvents(ScanProfile profile) {
        profile.clear();
        profile.setRgxOp(RgxOpTypes.CURSOR_OP);
        profile.setSortingProp(PropTypes.Time);
        profile.addRegExp(new RegExp(RegExp.EventTypes.ANY));
        profile.setDateRange(DateRanges.THIS_HOUR);

        profile.setName(String.format("Last events happened at %s o'clock", LogsOperations.getDatePresentation(profile)));
    }

}
