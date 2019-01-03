import 'whatwg-fetch'

export default {
    getDetails: (cb, rejected) => fetch('offender/detail', {cache: "no-store"})
        .then(response => response.json())
        .then(data => cb(data))
        .catch(error => rejected(error)),

    getRegistrations: (cb, rejected) => fetch('offender/registrations', {cache: "no-store"})
        .then(response => response.json())
        .then(data => cb(data))
        .catch(error => rejected(error))
}

