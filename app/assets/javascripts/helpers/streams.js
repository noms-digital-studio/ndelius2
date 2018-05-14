const flatMap = (a, cb) => [].concat(...a.map(cb))

const range = (count) => [...Array(count)].map((v, i) => i + 1)

const sort = (list, sorter) => {
    list.sort(sorter)
    return list
}

const alphabeticalOnProperty = prop => {
    return (a, b) => {
        const lowerA =  a[prop].toLowerCase()
        const lowerB =  b[prop].toLowerCase()

        if (lowerA > lowerB) {
            return 1;
        }
        if (lowerA < lowerB) {
            return -1;
        }
        return 0;
    }
}

export {flatMap, range, sort, alphabeticalOnProperty}