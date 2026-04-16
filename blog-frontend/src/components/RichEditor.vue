<template>
  <div class="rich-editor-container">
    <div ref="toolbarRef" class="editor-toolbar"></div>
    <div ref="editorRef" class="editor-content" :style="{ height: height }"></div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, watch, nextTick } from 'vue'
import { createEditor, createToolbar, DomEditor } from '@wangeditor/editor'
import '@wangeditor/editor/dist/css/style.css'
import { ElMessage } from 'element-plus'
import { fileApi } from '@/api'

const props = defineProps({
  modelValue: {
    type: String,
    default: ''
  },
  height: {
    type: String,
    default: '500px'
  },
  placeholder: {
    type: String,
    default: '请输入内容...'
  },
  mode: {
    type: String,
    default: 'default' // 或 'simple'
  }
})

const emit = defineEmits(['update:modelValue'])

const toolbarRef = ref()
const editorRef = ref()
let editor = null

watch(
  () => props.modelValue,
  (newValue) => {
    if (editor && newValue !== editor.getHtml()) {
      // 设置编辑器内容
      editor.clear()
      editor.dangerouslyInsertHtml(newValue)
    }
  }
)

onMounted(() => {
  initEditor()
})

const initEditor = async () => {
  // 创建编辑器实例
  editor = createEditor({
    selector: editorRef.value,
    config: {
      placeholder: props.placeholder,
      hoverbarKeys: {
        // 图片的 hoverbar 配置
        image: {
          menuKeys: [
            'imageWidth30',
            'imageWidth50',
            'imageWidth100',
            'imageFloatNone',
            'imageFloatLeft',
            'imageFloatRight',
            'imageFloatCenter',
            'deleteImage'
          ]
        }
      },
      MENU_CONF: {
        uploadImage: {
          allowedFileTypes: ['image/jpeg', 'image/jpg', 'image/png', 'image/gif', 'image/bmp', 'image/webp'],
          maxFileSize: 5 * 1024 * 1024, // 5MB
          base64LimitSize: 5 * 1024 * 1024, // 5MB
          // 自定义上传
          customUpload: async (file, insertFn) => {
            // 验证文件类型
            const allowedTypes = ['image/jpeg', 'image/jpg', 'image/png', 'image/gif', 'image/bmp', 'image/webp']
            if (!allowedTypes.includes(file.type)) {
              ElMessage.error('只能上传图片文件: jpg, jpeg, png, gif, bmp, webp')
              return
            }

            // 验证文件大小 (例如限制为 5MB)
            const maxSize = 5 * 1024 * 1024 // 5MB
            if (file.size > maxSize) {
              ElMessage.error('图片大小不能超过 5MB')
              return
            }

            const formData = new FormData()
            formData.append('file', file)
            
            try {
              const response = await fileApi.uploadFile(formData)
              console.log('上传响应:', response)
              
              // 根据API响应结构获取图片URL
              let imageUrl = ''
              console.log('响应数据结构:', response)
              
              // 由于axios拦截器已经处理了response，response本身即是服务器返回的数据
              // 所以 response.errno 应该直接存在
              if (response && typeof response === 'object') {
                if (response.errno === 0 && response.data && Array.isArray(response.data) && response.data.length > 0) {
                  imageUrl = response.data[0].url
                } else if (response.errno !== 0) {
                  throw new Error(response.message || '服务器返回错误状态')
                } else {
                  throw new Error(`服务器响应格式不正确: ${JSON.stringify(response)}`)
                }
              } else {
                throw new Error('服务器响应格式错误：不是有效的JSON对象')
              }
              
              // 构建完整的图片URL
              let fullImageUrl = imageUrl
              if (!imageUrl.startsWith('http')) {
                if (imageUrl.startsWith('/')) {
                  fullImageUrl = `${import.meta.env.VITE_API_BASE_URL || 'http://localhost:8081'}${imageUrl}`
                } else {
                  fullImageUrl = `${import.meta.env.VITE_API_BASE_URL || 'http://localhost:8081'}/${imageUrl}`
                }
              }
              
              // 插入图片到编辑器
              insertFn(fullImageUrl, '', '')
              ElMessage.success('图片上传成功')
            } catch (error) {
              console.error('图片上传失败:', error)
              console.error('完整错误对象:', error)
              ElMessage.error('图片上传失败：' + (error.message || '上传接口错误'))
            }
          }
        },
        // 配置粘贴选项
        paste: {
          // 是否允许粘贴图片
          pasteImage: true,
          // 是否允许粘贴HTML
          pasteHtml: true,
          // 自定义粘贴文本内容
          checkPaste: (editor, event) => {
            // 允许粘贴
            return true
          }
        }
      }
    },
    html: props.modelValue,
    // 启用表情、链接、表格等功能
    extendConfig: {
      // 配置菜单栏
      MENU_CONF: {}
    }
  })

  // 创建工具栏
  createToolbar({
    editor,
    selector: toolbarRef.value,
    config: {
      // 配置菜单项的排除，保留基础功能
      excludeKeys: [
        'uploadVideo'   // 禁用视频上传
      ]
    },
    mode: props.mode
  })

  // 监听编辑器内容变化
  editor.on('change', () => {
    const html = editor.getHtml()
    emit('update:modelValue', html)
  })
}

onBeforeUnmount(() => {
  if (editor) {
    editor.destroy()
  }
})
</script>

<style scoped>
.rich-editor-container {
  border: 1px solid #ccc;
  border-radius: 4px;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  height: v-bind('props.height');
}

.editor-toolbar {
  border-bottom: 1px solid #ccc;
  background-color: #f5f5f5;
  flex-shrink: 0; /* 工具栏不收缩 */
}

.editor-content {
  flex: 1;
  min-height: 300px;
  background-color: white;
  overflow-y: auto;
}

/* 自定义滚动条样式 */
.editor-content::-webkit-scrollbar {
  width: 8px;
}

.editor-content::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 4px;
}

.editor-content::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 4px;
}

.editor-content::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}
</style>