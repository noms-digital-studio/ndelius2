const openPopupWindow = window.openPopup = (url, name, top, left) => {
  window.open(url, (name || 'reportpopup'), `top=${ top || '200' },left=${ left || '0' },width=1024,height=768,resizable=yes,scrollbars=yes,location=no,menubar=no,status=yes,toolbar=no`).focus()
}

export {
  openPopupWindow
}
