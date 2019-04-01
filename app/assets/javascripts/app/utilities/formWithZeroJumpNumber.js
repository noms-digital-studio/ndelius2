const formWithZeroJumpNumber = $form => {
  const formData = new FormData($form)
  formData.append('jumpNumber', '0')
  return formData
}

export {
  formWithZeroJumpNumber
}
