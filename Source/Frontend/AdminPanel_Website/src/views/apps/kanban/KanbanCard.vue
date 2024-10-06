<script setup>
const props = defineProps({
  item: {
    type: null,
    required: true,
  },
  boardId: {
    type: Number,
    required: true,
  },
  boardName: {
    type: String,
    required: true,
  },
})

const emit = defineEmits(['deleteKanbanItem'])

const resolveLabelColor = {
  'UX': 'success',
  'Image': 'warning',
  'Code Review': 'error',
  'Dashboard': 'info',
  'App': 'secondary',
  'Charts & Maps': 'primary',
}

const moreOptions = [
  {
    title: 'Copy Task link',
    href: '#',
  },
  {
    title: 'Duplicate Task',
    href: '#',
  },
  {
    title: 'Delete',
    onClick: () => {
      emit('deleteKanbanItem', {
        item: props.item,
        boardId: props.boardId,
        boardName: props.boardName,
      })
    },
  },
]
</script>

<template>
  <VCard
    v-if="item"
    :ripple="false"
    :link="false"
    class="kanban-card position-relative"
  >
    <VCardText class="d-flex flex-column gap-2">
      <div class="d-flex align-start gap-2">
        <div
          v-if="item.labels && item.labels.length"
          class="d-flex flex-wrap gap-2"
        >
          <VChip
            v-for="text in item.labels"
            :key="text"
            size="small"
            :color="resolveLabelColor[text]"
          >
            {{ text }}
          </VChip>
        </div>

        <VSpacer />

        <VMenu>
          <template #activator="{ props: p, isActive }">
            <VIcon
              v-bind="p"
              icon="tabler-dots-vertical"
              class="position-absolute more-options"
              style="inset-block-start: 16px; inset-inline-end: 10px;"
              :style="isActive ? 'opacity: 1' : ''"
              size="20"
              @click.stop
            />
          </template>
          <VList
            :items="moreOptions"
            item-props
          />
        </VMenu>
      </div>

      <!-- Task Img -->
      <VImg
        v-if="item.image && item.image.length"
        :src="item.image"
        class="rounded"
      />

      <!-- Task title -->
      <p class="text-base text-high-emphasis mb-0">
        {{ item.title }}
      </p>

      <!-- footer  -->
      <div class="task-footer d-flex align-center flex-wrap justify-space-between">
        <div
          v-if="item.attachments || item.commentsCount"
          class="d-flex align-center gap-4"
        >
          <div v-if="item.attachments">
            <VIcon
              size="20"
              icon="tabler-paperclip"
              class="me-1"
            />
            <span class="text-body-1 d-inline-block">{{ item.attachments }}</span>
          </div>

          <div v-if="item.commentsCount">
            <VIcon
              size="20"
              icon="tabler-message-2"
              class="me-1"
            />
            <span class="text-body-1 d-inline-block">{{ item.commentsCount }}</span>
          </div>
        </div>

        <div
          v-if="item.members && item.members.length"
          class="v-avatar-group"
        >
          <VAvatar
            v-for="avatar in item.members"
            :key="avatar.name"
            size="30"
          >
            <VImg :src="avatar.img" />
            <VTooltip activator="parent">
              {{ avatar.name }}
            </VTooltip>
          </VAvatar>
        </div>
      </div>
    </VCardText>
  </VCard>
</template>

<style lang="scss" scoped>
.kanban-card {
  cursor: grab;

  :active {
    cursor: grabbing;
  }

  &[style^="z-index"] {
    cursor: grabbing !important;
  }

  .more-options {
    opacity: 0;
  }

  &:hover .more-options {
    opacity: 1;
  }
}
</style>
