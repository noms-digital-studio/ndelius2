const matches = (text, searchTerm) =>
  text && searchTerm.split(' ')
    .filter(searchWord => searchWord) // remove empty terms
    .map(searchWord => RegExp(searchWord, 'i').test(text))
    .reduce((accumulator, currentValue) => accumulator || currentValue, false)

export { matches }
