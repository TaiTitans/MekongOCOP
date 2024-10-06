import { HttpResponse, http } from 'msw'
import { database } from '@db/apps/kanban/db'

export const handlerAppsKanban = [
  // 👉 get all kanban data
  http.get('/api/apps/kanban', () => {
    return HttpResponse.json(database, { status: 200 })
  }),

  // 👉 rename board
  http.put('/api/apps/kanban/board/rename', async ({ request }) => {
    const boardData = await request.json()

    database.boards = database.boards.map(board => {
      if (board.id === boardData.boardId)
        board.title = boardData.newName
      
      return board
    })
    
    return new HttpResponse(null, { status: 201 })
  }),

  // 👉 delete board
  http.delete('/api/apps/kanban/board/:id', async ({ params }) => {
    const boardId = Number(params.id)

    database.boards = database.boards.filter(board => board.id !== boardId)
    
    return new HttpResponse(null, { status: 204 })
  }),

  // 👉 add new board
  http.post('/api/apps/kanban/board/add', async ({ request }) => {
    const boardName = await request.json()

    const getNewBoardId = () => {
      const newBoardId = database.boards.length + 1
      if (!(database.boards.some(board => board.id === newBoardId)))
        return newBoardId
      else
        return newBoardId + 1
    }

    if (database.boards.some(board => board.title === boardName.title)) {
      return HttpResponse.error()
    }
    else {
      database.boards.push({
        id: getNewBoardId(),
        title: boardName.title,
        itemsIds: [],
      })
      
      return new HttpResponse(null, { status: 201 })
    }
  }),

  // 👉 add new item
  http.post('/api/apps/kanban/item/add', async ({ request }) => {
    const newItem = await request.json()
    const itemId = database.items[database.items.length - 1].id + 1
    if (newItem.itemTitle && newItem.boardName) {
      // Add the new item to the items list
      database.items.push({
        id: itemId,
        title: newItem.itemTitle,
        attachments: 0,
        comments: '',
        commentsCount: 0,
        dueDate: '',
        labels: [],
        members: [],
      })


      // find the index of board in the database
      const boardId = database.boards.findIndex(board => board.id === newItem.boardId)


      // Add the new item to the board
      database.boards[boardId].itemsIds.push(itemId)
    }
    else {
      return HttpResponse.error()
    }
    
    return new HttpResponse(null, { status: 201 })
  }),

  // 👉 update item
  http.put('/api/apps/kanban/item/update', async ({ request }) => {
    const itemData = await request.json()

    database.items.forEach(item => {
      if (itemData.item && item.id === itemData.item.id) {
        item.title = itemData.item.title
        item.attachments = itemData.item.attachments
        item.comments = itemData.item.comments
        item.commentsCount = itemData.item.commentsCount
        item.dueDate = itemData.item.dueDate
        item.labels = itemData.item.labels
        item.members = itemData.item.members
      }
    })
    
    return new HttpResponse(null, { status: 201 })
  }),

  // 👉 delete item
  http.delete('/api/apps/kanban/item/:id', async ({ params }) => {
    const itemId = Number(params.id)

    database.items = database.items.filter(item => item.id !== itemId)
    database.boards.forEach(board => {
      board.itemsIds = board.itemsIds.filter(id => id !== itemId)
    })
    
    return new HttpResponse(null, { status: 204 })
  }),

  // 👉 update item state
  http.put('/api/apps/kanban/item/state-update', async ({ request }) => {
    const stateData = await request.json()

    database.boards.forEach(board => {
      if (board.id === stateData.boardId)
        board.itemsIds = stateData.ids
    })
    
    return new HttpResponse(null, { status: 201 })
  }),

  // 👉 update board state
  http.put('/api/apps/kanban/board/state-update', async ({ request }) => {
    const boardState = await request.json()


    // sort board as per boardState
    const sortedBoards = boardState.map(boardId => {
      return database.boards.find(board => board.id === boardId)
    })

    database.boards = sortedBoards
    
    return new HttpResponse(null, { status: 201 })
  }),
]
