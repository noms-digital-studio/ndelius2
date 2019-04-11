import { debounce } from '../utilities/debounce'

const startSaveIcon = () => {
  const $spinner = document.querySelector('.moj-auto-save__spinner')
  $spinner.classList.remove('error')
  $spinner.classList.add('active')
  document.getElementById('save_indicator').classList.remove('govuk-visually-hidden')
}

const endSaveIcon = () => {
  const $spinner = document.querySelector('.moj-auto-save__spinner')
  $spinner.classList.remove('error')
  $spinner.classList.remove('active')

  debounce(() => {
    document.getElementById('save_indicator').classList.add('govuk-visually-hidden')
  }, 2000)()
}

const saveIconError = () => {
  const $spinner = document.querySelector('.moj-auto-save__spinner')
  $spinner.classList.add('error')
  $spinner.classList.remove('active')
  document.getElementById('save_indicator').classList.remove('govuk-visually-hidden')
}

export {
  startSaveIcon,
  endSaveIcon,
  saveIconError
}
