import 'whatwg-fetch'

export default {
  getDetails: (cb, rejected) => fetch('offender/detail', options)
    .then(jsonOrError)
    .then(data => cb(data))
    .catch(error => rejected(error)),

  getRegistrations: (cb, rejected) => fetch('offender/registrations', options)
    .then(jsonOrError)
    .then(data => cb(data))
    .catch(error => rejected(error)),

  getConvictions: (cb, rejected) => fetch('offender/convictions', options)
    .then(jsonOrError)
    .then(data => cb(data))
    .catch(error => rejected(error)),

  getNextAppointment: (cb, noDataCb, rejected) => fetch('offender/nextAppointment', options)
    .then(jsonOrError)
    .then(data => cb(data))
    .catch(error => error.message === '404' ? noDataCb() : rejected(error)),

  getPersonalCircumstances: (cb, rejected) => fetch('offender/personalCircumstances', options)
    .then(jsonOrError)
    .then(data => cb(data))
    .catch(error => rejected(error))
}

const jsonOrError = response => {
  if (response.ok) {
    return response.json()
  } else if (response.status === 404) {
    throw new Error('404')
  } else {
    throw response.error()
  }
}

const options = {
  credentials: 'same-origin',
  cache: 'no-store'
}
