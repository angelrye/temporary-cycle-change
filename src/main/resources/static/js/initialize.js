const initialize = (function () {

    let userDivisions = [];

    function getUser() {
        return {
            username: document.querySelector("#userName").value,
            groups: document.querySelector("#userGroup").value,
            division: document.querySelector("#userDivision").value,
            isAdmin: new Boolean(document.querySelector("#isAdmin").value == 'true'),
            fullname: document.querySelector("#fullname").value
        }
    }

    function initializeComponents() {
        initializeComponentsForNonAdminUsers();
    }

    function initializeComponentsForNonAdminUsers() {
        if (getUser().isAdmin == true) {
            $(".admin").each(function (element) {
                $(this).removeAttr('hidden');
            });

            $(".rim").each(function (element) {
                $(this).hide();
            });
        }
    }

    function setDivisions(divisions) {
        let arrDiv = divisions.map(div => {
            return div.content;
        });

        userDivisions = arrDiv;
    }

    function getDivisions() {
        return userDivisions;
    }

    function getDivisionsExcept(excludeDivision) {
        const filtered = [];
        userDivisions.filter(division => {
            if (division !== excludeDivision) {
                filtered.push(division);
            }
        });

        return filtered;
    }

    return {
        initializeComponentsForNonAdminUsers: initializeComponentsForNonAdminUsers,
        initializeComponents: initializeComponents,
        getUser: getUser,
        setDivisions: setDivisions,
        getDivisions: getDivisions,
        getDivisionsExcept : getDivisionsExcept
    }
})();

const restPaths = (function (user) {
    const PROTOCOL = window.location.protocol;
    const HOST = window.location.host;
    const REST_PATH = '/tcc/rest';
    const REST_FULL_PATH = PROTOCOL + '//' + HOST + REST_PATH;

    const REST_REQUEST_STATUS_SUCCESS = 'success';
    const REST_REQUEST_STATUS_ERROR = 'error';
    const REST_REQUEST_STATUS_INFO = 'info';

    function getHeader(method) {
        if (method === 'GET') {
            return {
                'APP_USER': user.username,
                'USER_DIVISION_CODE': user.division,
                'USER_GROUPS': user.groups//,
                // 'xTest' : 'Access Denied'
            }
        } else {
            return {
                'APP_USER': user.username,
                // 'APP_USER': 'test',
                'USER_DIVISION_CODE': user.division,
                'USER_GROUPS': user.groups,
                'Content-Type': 'application/json'//,
                // 'xTest' : 'Access Denied'
            }
        }
    }

    fetchDivisionUrl = () => REST_FULL_PATH + '/divisions';
    fetchBaseSchedule = (divisions, startDate, endDate) => {
        let arrUrls = new Array();
        divisions.map(division => {
            arrUrls.push(REST_FULL_PATH + `/cycleschedules/${division}?startDate=${startDate}&endDate=${endDate}`);   
        });

        return arrUrls;
    }

    newCycleChangeUrl = () => REST_FULL_PATH + '/cyclechanges';
    fetchCycleChangeSchedule = () => newCycleChangeUrl() + '/schedules';
    modifyCycleChangeSchedule = () => newCycleChangeUrl() + '/update';
    cancelCycleChangeScheduleURL = () => newCycleChangeUrl() + '/cancel/request';
    cancelRunURL = () => newCycleChangeUrl() + '/cancel/run';
    requestApprovalURL = () => newCycleChangeUrl() + '/forapproval';
    sendForApprovalViaEmailURL = () => requestApprovalURL() + "/sent-to-email";
    validateEntries = () => requestApprovalURL() + "/validate";
    approvalUrl = () => newCycleChangeUrl() + '/approve';
    rejectUrl = () => newCycleChangeUrl() + '/reject';
    emailReportUrl = () => REST_FULL_PATH + '/report';
    historyUrl = () => REST_FULL_PATH + '/history';

    getErrorStatus = () => REST_REQUEST_STATUS_ERROR;
    getSuccessStatus = () => REST_REQUEST_STATUS_SUCCESS;
    getInfoStatus = () => REST_REQUEST_STATUS_INFO;

    return {
        getHeader: getHeader,
        fetchDivisionUrl: fetchDivisionUrl,
        fetchBaseSchedule: fetchBaseSchedule,
        fetchCycleChangeSchedule: fetchCycleChangeSchedule,
        modifyCycleChangeSchedule: modifyCycleChangeSchedule,
        cancelCycleChangeScheduleURL: cancelCycleChangeScheduleURL,
        cancelRunURL: cancelRunURL,
        requestApprovalURL: requestApprovalURL,
        sendForApprovalViaEmailURL: sendForApprovalViaEmailURL,
        validateEntries: validateEntries,
        newCycleChangeUrl: newCycleChangeUrl,
        emailReportUrl: emailReportUrl,
        approvalUrl: approvalUrl,
        rejectUrl: rejectUrl,
        historyUrl: historyUrl,
        getErrorStatus: getErrorStatus,
        getSuccessStatus: getSuccessStatus,
        getInfoStatus: getInfoStatus
    }
})(initialize.getUser());