import { updateFormData } from '../helpers/formDataHelper'

const formWithZeroJumpNumber = $form => {
  const formData = new FormData($form)
  updateFormData(formData)
  formData.set('jumpNumber', '0')
  return formData
}

export {
  formWithZeroJumpNumber
}
