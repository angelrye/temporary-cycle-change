export async function evaluateAndReturnResponses(responses) {
    return await responses.map(evaluateAndReturnResponse);
}

export async function evaluateAndReturnResponse(response) {
    const data = await response.json();

    if (!response.ok) {
        if (data.error) {
            return Promise.reject(data.error);
        } else {
            return Promise.reject(data.message);
        }
    } else {
        return data;
    }

}

export async function evaluateEmailResponse(response) {
    const responseBackup = response.clone();

    if (!response.ok) {
        const data = await responseBackup.json();
        
        if (data.error) {
            return Promise.reject(data.error);
        } else {
            return Promise.reject(data.message);
        }
    } else {
        return Promise.resolve();
    }
}

export class HttpClient {
    static get(url) {
        const encodedUrl = encodeURI(url);

        return fetch(encodedUrl, {
            method: 'GET',
            cache: 'no-cache',
            headers: restPaths.getHeader('GET'),
            mode: 'cors'
        })

    }

    static getBatch(urls) {
        return (async () => {
            return await Promise.all(urls.map(url => {
                const encodedUrl = encodeURI(url);
                return fetch(encodedUrl, {
                    method: 'GET',
                    cache: 'no-cache',
                    headers: restPaths.getHeader('GET'),
                    mode: 'cors'
                })
            }));
        })();
    }

    static post(method, url, body) {
        return (async () => {
            return await fetch(url, {
                method: method,
                body: JSON.stringify(body),
                headers: restPaths.getHeader(method),
                mode: 'cors',
                referrer: 'no-referrer'
            });
        })();
    }

    static postBatch(method, url, bodies) {
        return (async () => {
            const promises = bodies.map(body => this.post(method, url, body));
            return await Promise.all(promises);
        })();
    }
}

export class SearchCycleChange {
    divisionId;
    startDate;
    endDate;

    constructor(divisionId, startDate, endDate) {
        this.divisionId = divisionId;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}

export class NewCycleChange {
    constructor(divisionId, runDate, effDate, offsite) {
        this.id = '0';
        this.divId = divisionId;
        this.runDate = runDate;
        this.effectiveDate = effDate;
        this.offsiteIndicator = offsite;
        this.comment = '';
    }
}

export class SearchCycleChangeByDateRange {
    constructor(startDate, endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }
}