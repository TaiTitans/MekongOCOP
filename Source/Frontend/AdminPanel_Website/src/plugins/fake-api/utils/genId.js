export const genId = array => {
  const { length } = array
  let lastIndex = 0
  if (length)
    lastIndex = Number(array[length - 1]?.id) + 1
  
  return lastIndex || (length + 1)
}
