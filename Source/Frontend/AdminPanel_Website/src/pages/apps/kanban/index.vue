<script setup>
import KanbanBoardComp from '@/views/apps/kanban/KanbanBoard.vue'

// 👉 initial kanban data fetch
const {
  data: kanban,
  execute: refetchKanban,
} = await useApi('/apps/kanban')

const addNewBoard = async newBoardName => {
  await $api('/apps/kanban/board/add', {
    method: 'POST',
    body: { title: newBoardName },
  })
  refetchKanban()
}

const deleteBoard = async boardId => {
  await $api(`/apps/kanban/board/${ boardId }`, { method: 'DELETE' })
  refetchKanban()
}

const renameTheBoard = async kanbanBoard => {
  await $api('/apps/kanban/board/rename', {
    method: 'PUT',
    body: kanbanBoard,
  })
  refetchKanban()
}

const addNewItem = async newItem => {
  await $api('/apps/kanban/item/add', {
    method: 'POST',
    body: newItem,
  })
  refetchKanban()
}

const editItemFn = async editItem => {
  await $api('/apps/kanban/item/update', {
    method: 'PUT',
    body: editItem,
  })
  refetchKanban()
}

const deleteItemFn = async deleteItem => {
  if (deleteItem.item && deleteItem.item.id) {
    await $api(`/apps/kanban/item/${ deleteItem.item.id }`, {
      method: 'DELETE',
      body: deleteItem,
    })
    refetchKanban()
  }
}

const updateItemState = async kanbanState => {
  await $api('/apps/kanban/item/state-update', {
    method: 'PUT',
    body: kanbanState,
  })
}

const updateBoardState = async kanbanBoardIds => {
  await $api('/apps/kanban/board/state-update', {
    method: 'PUT',
    body: kanbanBoardIds,
  })
}
</script>

<template>
  <KanbanBoardComp
    v-if="kanban"
    :kanban-data="kanban"
    @add-new-board="addNewBoard"
    @delete-board="deleteBoard"
    @rename-board="renameTheBoard"
    @add-new-item="addNewItem"
    @edit-item="editItemFn"
    @delete-item="deleteItemFn"
    @update-items-state="updateItemState"
    @update-board-state="updateBoardState"
  />
</template>
