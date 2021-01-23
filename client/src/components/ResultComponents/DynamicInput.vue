<template>
  <div>
    <nav class="level spacer">
      <div class="level-left">
        {{ question }} ({{formattedType}}) <span v-if="unit">({{ unit }})</span>
      </div>
      <div class="level-right" v-if="viewType==='edit'">
        <b-field position="is-right" class="buttons">
          <b-button
            size="is-small"
            id="add-answer-btn"
            icon-left="plus"
            @click="addPossibleAnswer"
            class="button is-success"
          >Add
          </b-button>
          <b-button
            size="is-small"
            id="remove-answer-btn"
            icon-left="minus"
            @click="removeLastAnswer"
            class="button is-info"
          >Remove
          </b-button>
        </b-field>
      </div>
    </nav>
    <div v-if="viewType!=='answer'">
      <b-field
        :type="!triggerResultValidationTemp ? 'is-danger' : ''"
        :message="message"
        :key="index"
        v-for="(v, index) in $v.answers.$each.$iter"
      >
        <div>
          <b-input
            expanded
            :has-counter="false"
            maxlength="50"
            :readonly="readOnly"
            :icon="icon"
            @input="$emit('input', answers)"
            v-model="v.answer.$model"
          />
          <p
            class="has-text-danger err-msg is-size-7"
            v-if="$v.answers.$each[index].$invalid"
          >Field is required</p>
        </div>
      </b-field>
    </div>

    <div v-if="viewType==='answer'">
      <div v-if="type==='multichoice_combination'">
        <b-field>
          <div>
            <b-checkbox
              :key="index"
              :disabled="!viewingSelfAnswer"
              v-for="(item, index) in answers"
              :native-value="item.answer"
              v-model="checkboxSelectedAnswers"
              @input="emitArray($event)"
            >{{ answers[index].answer }}</b-checkbox>
          </div>
        </b-field>
      </div>
      <div v-if="viewType==='answer'">
        <div v-if="type==='checkbox'">
          <b-field>
            <div>
              <b-checkbox
                :disabled="!viewingSelfAnswer"
                :key="index" v-for="(item, index) in answers"
                :native-value="item.answer"
                v-model="checkboxAnswer"
                @input="emitArray($event)"
              >
                {{ answers[index].answer }}
              </b-checkbox>
            </div>
          </b-field>
        </div>
        <div v-if="type==='multichoice_single'">
          <b-field>
            <div>
              <b-radio
                :disabled="!viewingSelfAnswer"
                :key="index" v-for="(item, index) in answers"
                @input="$emit('answered', [$event.id.toString()])"
                v-model="radioAnswer"
                :native-value="item">
                {{ answers[index].answer }}
              </b-radio>
            </div>
          </b-field>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
  import {required, minLength} from "vuelidate/lib/validators";

  export default {
    name: "DynamicInput",
    props: [
      "question",
      "type",
      "unit",
      "viewType",
      "triggerResultValidation",
      "userAnswer",
      "viewingSelfAnswer"
    ],
    data() {
      return {
        radioAnswer: null,
        readOnly: true,
        checkboxAnswer: [],
        answers: [{answer: ""}],
        triggerResultValidationTemp: true,
        radioSelectedAnswer: "",
        checkboxSelectedAnswers: []
      };
    },
    watch: {
      triggerResultValidation: function () {
        this.triggerResultValidationTemp = this.triggerResultValidation;
      },
      userAnswer: {
        deep: true,
        handler() {
          if (this.userAnswer.length === 0) { return; }
          let answer = this.userAnswer[0].answer
          if (this.type === 'multichoice_single') {
            const idx = this.answers.map(a => a.id).indexOf(Number(answer[0]))
            this.radioAnswer = this.answers[idx]
          } else if (this.type == 'multichoice_combination') {
            answer.forEach(ans => {
              const idx = this.answers.map(a => a.id).indexOf(Number(ans))
              this.checkboxSelectedAnswers.push(this.answers[idx].answer)
            })
          }
        }

      },
      answers: {
        deep: true,
        handler() {
          this.$emit("isValid", !this.$v.$invalid);
        }
      }
    },
    computed: {
      formattedType() {
        switch (this.type) {
          case "multichoice_combination":
            return "multi-choice combination";
          case "multichoice_single":
            return "multi-choice single";
          default:
            return this.type;
        }
      },

      icon() {
        if (this.type === "multichoice_combination") {
          return "check-square";
        } else {
          return "dot-circle";
        }
      },
      requiresTwoOptions() {
        return ["multichoice_combination", "multichoice_single"].includes(
          this.type
        );
      },
      message() {
        if (!this.$v.answers.minLength) {
          return "At least 2 answers are required";
        }
        return "";
      },

    },
    validations: {
      answers: {
        minLength: minLength(2),
        $each: {
          answer: {
            required
          }
        }
      }
    },
    mounted() {
      if (["edit", "answer"].includes(this.viewType)) {
        this.readOnly = false;
      }
      if (this.userAnswer) {
        this.answers = [];
        this.userAnswer.forEach(a => {
          this.answers.push(a);
        });
      } else if (this.requiresTwoOptions) {
        this.answers.push({answer: ""});
      }
    },
    methods: {
      emitArray(answers) {
        let answerList = []
        answers.forEach(x => {
          const id = this.answers.find(y => y.answer === x).id;
          answerList.push(id.toString())
        });
        this.$emit("answered", answerList);
      },

      addPossibleAnswer() {
        if (!this.answers[this.answers.length - 1].answer.trim()) {
          return;
        }

        this.answers.push({answer: ""});
      },

      removeLastAnswer() {
        if (this.requiresTwoOptions && this.answers.length <= 2) {
          return;
        }
        if (this.answers.length > 1) {
          this.answers.pop();
        }
      }
    }
  };
</script>

<style scoped>
  nav.spacer {
    margin: auto;
    margin-bottom: 0.5rem;
  }

  .err-msg {
    margin-top: 0.5rem;
  }
</style>
