<script setup>
import {
  animations,
  handleEnd,
  performTransfer,
} from '@formkit/drag-and-drop'
import { dragAndDrop } from '@formkit/drag-and-drop/vue'
import { VForm } from 'vuetify/components/VForm'
import KanbanCard from '@/views/apps/kanban/KanbanCard.vue'

const props = defineProps({
  kanbanIds: {
    type: Array,
    required: true,
  },
  groupName: {
    type: String,
    required: true,
  },
  boardName: {
    type: String,
    required: true,
  },
  boardId: {
    type: Number,
    required: true,
  },
  kanbanData: {
    type: null,
    required: true,
  },
})

const emit = defineEmits([
  'renameBoard',
  'deleteBoard',
  'addNewItem',
  'editItem',
  'updateItemsState',
  'deleteItem',
])

const refKanbanBoard = ref()
const localBoardName = ref(props.boardName)
const localIds = ref(props.kanbanIds)
const isAddNewFormVisible = ref(false)
const isBoardNameEditing = ref(false)
const refForm = ref()
const newTaskTitle = ref('')
const refKanbanBoardTitle = ref()

// 👉 required validator
const boardActions = [
  {
    title: 'Rename',
    prependIcon: 'tabler-pencil',
    onClick: () => {
      isBoardNameEditing.value = true
    },
  },
  {
    title: 'Delete',
    prependIcon: 'tabler-trash',
    onClick: () => emit('deleteBoard', props.boardId),
  },
]

// 👉 emit rename board event
const renameBoard = () => {
  refKanbanBoardTitle.value?.validate().then(valid => {
    if (valid.valid) {
      emit('renameBoard', {
        oldName: props.boardName,
        newName: localBoardName.value,
        boardId: props.boardId,
      })
      isBoardNameEditing.value = false
    }
  })
}

// 👉 emit add new item event
const addNewItem = () => {
  refForm.value?.validate().then(valid => {
    if (valid.valid) {
      emit('addNewItem', {
        itemTitle: newTaskTitle.value,
        boardName: props.boardName,
        boardId: props.boardId,
      })
      isAddNewFormVisible.value = false
      newTaskTitle.value = ''
    }
  })
}

// 👉 initialize draggable
dragAndDrop({
  parent: refKanbanBoard,
  values: localIds,
  group: props.groupName,
  draggable: child => child.classList.contains('kanban-card'),
  plugins: [animations()],
  performTransfer: (state, data) => {
    performTransfer(state, data)
    emit('updateItemsState', {
      boardId: props.boardId,
      ids: localIds.value,
    })
  },
  handleEnd: data => {
    handleEnd(data)
    emit('updateItemsState', {
      boardId: props.boardId,
      ids: localIds.value,
    })
  },
})

// 👉 watch kanbanIds its is useful when you add new task
watch(() => props, () => {
  localIds.value = props.kanbanIds
}, {
  immediate: true,
  deep: true,
})

const resolveItemUsingId = id => props.kanbanData.items.find(item => item.id === id)

const deleteItem = item => {
  emit('deleteItem', item)
}

// 👉 reset add new item form when esc or close
const hideAddNewForm = () => {
  isAddNewFormVisible.value = false
  refForm.value?.reset()
}

// close add new item form when you loose focus from the form
onClickOutside(refForm, hideAddNewForm)

// close board name form when you loose focus from the form
onClickOutside(refKanbanBoardTitle, () => {
  isBoardNameEditing.value = false
})

// 👉 reset board rename form when esc or close
const hideResetBoardNameForm = () => {
  isBoardNameEditing.value = false
  localBoardName.value = props.boardName
}

const handleEnterKeydown = event => {
  if (event.key === 'Enter' && !event.shiftKey)
    addNewItem()
}
</script>

<template>
  <div class="kanban-board">
    <!-- 👉 board heading and title -->
    <div class="kanban-board-header pb-4">
      <VForm
        v-if="isBoardNameEditing"
        ref="refKanbanBoardTitle"
        @submit.prevent="renameBoard"
      >
        <VTextField
          v-model="localBoardName"
          autofocus
          variant="underlined"
          :rules="[requiredValidator]"
          hide-details
          class="border-0"
          @keydown.esc="hideResetBoardNameForm"
        >
          <template #append-inner>
            <VIcon
              size="20"
              color="success"
              icon="tabler-check"
              class="me-1"
              @click="renameBoard"
            />

            <VIcon
              size="20"
              color="error"
              icon="tabler-x"
              @click="hideResetBoardNameForm"
            />
          </template>
        </VTextField>
      </VForm>

      <div
        v-else
        class="d-flex align-center justify-space-between "
      >
        <h4 class="text-lg font-weight-medium text-truncate">
          {{ boardName }}
        </h4>

        <div class="d-flex align-center">
          <VIcon
            class="drag-handler"
            size="20"
            icon="tabler-arrows-move"
          />

          <MoreBtn
            size="28"
            icon-size="20"
            class="text-high-emphasis"
            :menu-list="boardActions"
            item-props
          />
        </div>
      </div>
    </div>

    <!-- 👉 draggable task start here -->
    <div
      v-if="localIds"
      ref="refKanbanBoard"
      class="kanban-board-drop-zone rounded d-flex flex-column gap-4"
      :class="localIds.length ? 'mb-4' : ''"
    >
      <template
        v-for="id in localIds"
        :key="id"
      >
        <KanbanCard
          :item="resolveItemUsingId(id)"
          :board-id="props.boardId"
          :board-name="props.boardName"
          @delete-kanban-item="deleteItem"
          @click="emit('editItem', { item: resolveItemUsingId(id), boardId: props.boardId, boardName: props.boardName })"
        />
      </template>

      <!-- 👉 Add new Form -->
      <div class="add-new-form">
        <h6
          class="text-base font-weight-regular cursor-pointer ms-4"
          @click="isAddNewFormVisible = !isAddNewFormVisible"
        >
          <VIcon
            size="15"
            icon="tabler-plus"
          /> Add New Item
        </h6>

        <VForm
          v-if="isAddNewFormVisible"
          ref="refForm"
          class="mt-4"
          validate-on="submit"
          @submit.prevent="addNewItem"
        >
          <div class="mb-4">
            <VTextarea
              v-model="newTaskTitle"
              :rules="[requiredValidator]"
              placeholder="Add Content"
              autofocus
              rows="2"
              @keydown.enter="handleEnterKeydown"
              @keydown.esc="hideAddNewForm"
            />
          </div>
          <div class="d-flex gap-4 flex-wrap">
            <VBtn
              size="small"
              type="submit"
            >
              Add
            </VBtn>
            <VBtn
              size="small"
              variant="tonal"
              color="secondary"
              @click="hideAddNewForm"
            >
              Cancel
            </VBtn>
          </div>
        </VForm>
      </div>
    </div>
  </div>
</template>

<style lang="scss">
.kanban-board-header {
  .drag-handler {
    cursor: grab;
    opacity: 0;

    &:active {
      cursor: grabbing;
    }
  }

  &:hover {
    .drag-handler {
      opacity: 1;
    }
  }
}
</style>
