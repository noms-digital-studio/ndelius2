import 'whatwg-fetch'

export default {
    getDetails: (cb) => fetch('../offender/detail', {cache: "no-store"})
        .then(response => response.json())
        .then(data => cb(data))
        .catch(error => console.error(error))
}

