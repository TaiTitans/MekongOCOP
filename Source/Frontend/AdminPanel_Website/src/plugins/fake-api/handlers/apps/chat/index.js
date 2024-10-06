import { HttpResponse, http } from 'msw'
import { db } from '@db/apps/chat/db'

export const handlerAppsChat = [
  http.get(('/api/apps/chat/chats-and-contacts'), ({ request }) => {
    const url = new URL(request.url)
    const q = url.searchParams.get('q') || ''
    const qLowered = q.toLowerCase()

    const chatsContacts = db.chats
      .map(chat => {
        const contact = JSON.parse(JSON.stringify(db.contacts.find(c => c.id === chat.userId)))

        contact.chat = { id: chat.id, unseenMsgs: chat.unseenMsgs, lastMessage: chat.messages.at(-1) }
        
        return contact
      })
      .reverse()

    const profileUserData = db.profileUser

    const response = {
      chatsContacts: chatsContacts.filter(c => c.fullName.toLowerCase().includes(qLowered)),
      contacts: db.contacts.filter(c => c.fullName.toLowerCase().includes(qLowered)),
      profileUser: profileUserData,
    }

    return HttpResponse.json(response, { status: 200 })
  }),
  http.get(('/api/apps/chat/chats/:userId'), ({ params }) => {
    const userId = Number(params.userId)
    const chat = db.chats.find(e => e.userId === userId)
    if (chat)
      chat.unseenMsgs = 0
    
    return HttpResponse.json({
      chat,
      contact: db.contacts.find(c => c.id === userId),
    }, {
      status: 200,
    })
  }),
  http.post(('/api/apps/chat/chats/:userId'), async ({ request, params }) => {
    // Get user id from URL
    const chatId = Number(params.userId)

    // Get message from post data
    const { message, senderId } = await request.json()
    let activeChat = db.chats.find(chat => chat.userId === chatId)

    const newMessageData = {
      message,
      time: String(new Date()),
      senderId,
      feedback: {
        isSent: true,
        isDelivered: false,
        isSeen: false,
      },
    }


    // If there's new chat for user create one
    let isNewChat = false
    if (activeChat === undefined) {
      isNewChat = true
      db.chats.push({
        id: db.chats.length + 1,
        userId: chatId,
        unseenMsgs: 0,
        messages: [newMessageData],
      })
      activeChat = db.chats.at(-1)
    }
    else {
      activeChat.messages.push(newMessageData)
    }
    const response = { msg: newMessageData }
    if (isNewChat)
      response.chat = activeChat
    
    return HttpResponse.json(response, { status: 201 })
  }),
]
