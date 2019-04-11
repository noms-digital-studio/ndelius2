import { nodeListForEach } from '../utilities/nodeListForEach'
import { promisifyXMLHttpRequest } from '../utilities/xhrPromisify'
import { formWithZeroJumpNumber } from '../utilities/formWithZeroJumpNumber'
import { endSaveIcon, saveIconError, startSaveIcon } from '../components/saveIcon'

function hideAutoSaveErrors () {
  nodeListForEach(document.querySelectorAll('.govuk-form-group--error'), $element => {
    $element.classList.remove('govuk-form-group--error')
  })
  nodeListForEach(document.querySelectorAll('[id$=\'-autosave_error\']'), $element => {
    $element.classList.add('govuk-visually-hidden')
  })
}

const autoSaveProgress = $editor => {
  const $form = document.getElementById('ndForm')
  const errorMessage = document.getElementById(`${ $editor.id }-autosave_error`)
  startSaveIcon()
  saveReportProgress($form).then(() => {
    endSaveIcon()
    hideAutoSaveErrors()
  }, () => {
    saveIconError()
    errorMessage.classList.remove('govuk-visually-hidden')
    errorMessage.closest('.govuk-form-group').classList.add('govuk-form-group--error')
  })
}

const saveReportProgress = ($form) => {
  return promisifyXMLHttpRequest({
    method: 'POST',
    url: `${ $form.getAttribute('action') }/save`,
    body: formWithZeroJumpNumber($form)
  })
}

export {
  autoSaveProgress,
  saveReportProgress
}