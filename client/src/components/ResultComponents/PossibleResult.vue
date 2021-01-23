<template>
  <div>
    <div class="box" v-if="viewType==='edit'">
      <b-field
        id="input-box"
        :type="isErrorVisible ? 'is-danger' : ''"
        label="Question"
        label-position="on-border"
      >
        <b-input
          maxlength="200"
          @input="isErrorVisible = false"
          placeholder="Enter your question here"
          expanded
          v-model="question"
        ></b-input>
        <b-select placeholder="Input type" v-model="option">
          <option>Text</option>
          <option>Number</option>
          <option>Checkbox</option>
          <option>Multi-Choice</option>
          <option>Date</option>
          <option>Time</option>
        </b-select>
      </b-field>
      <br />

      <div>
        <div v-if="this.option === 'Number'" class="columns">
          <div class="column is-half">
            <b-field
              id="units"
              label="Measurement Unit"
              :type="isErrorVisible ? 'is-danger' : ''"
              label-position="on-border"
              expanded
            >
              <b-input
                maxlength="20"
                @input="isErrorVisible = false"
                placeholder="Enter a unit of measurement"
                expanded
                v-model="measurementUnit"
              ></b-input>
            </b-field>
          </div>
          <div class="column is-half number-switch">
            <b-switch
              v-model="isNumberDecimal"
            >&nbsp;{{isNumberDecimal ? 'Decimals allowed' : 'Whole numbers only'}}</b-switch>
          </div>
        </div>
        <div v-if="this.option === 'Multi-Choice'">
          <b-switch
            v-model="isMultiChoiceCombo"
          >&nbsp;{{isMultiChoiceCombo ? 'Combination' : 'Single'}}</b-switch>
          <b-tooltip
            label="Single requires that users choose a single option. Combination allows users to select as many as needed"
          >
            <b-icon icon="question-circle"></b-icon>
          </b-tooltip>
        </div>
      </div>
      <p class="level-right">
        <b-button id="add-btn" icon-right="plus" @click="addQuestion" type="is-success">Add</b-button>
      </p>
    </div>

    <b-field>
      <div v-if="viewType==='edit'">
        <b-field :message="message" :type="isErrorVisible ? 'is-danger' : ''" />
      </div>
    </b-field>


    <div :key="index" v-for="(q, index) in questions">
      <div class="columns">
        <div class="column delete-btn is-narrow" v-if="viewType==='edit'">
          <b-tooltip
            :label="questions[index].answered ? 'Cannot delete questions with associated answers' :''"
          >
            <b-button
              :disabled="questions[index].answered === true"
              outlined
              size="is-small"
              icon-left="times"
              @click="removeQuestion(index)"
              type="is-danger"
            ></b-button>
          </b-tooltip>
        </div>
        <div class="column spacer box">
          <div v-if="isSingleInput(q.type)">
            <SingleInput
              :viewingSelfAnswer = "viewingSelfAnswer"
              :question="q.question"
              :type="q.type"
              :unit="q.unit"
              :viewType="viewType"
              :userAnswer="q.answers"
              @isValid="isResultValid($event, index)"
              @input="appendAnswer(q, $event)"
            />
          </div>
          <div v-else>
            <DynamicInput
              :viewingSelfAnswer = "viewingSelfAnswer"
              :unit="q.unit"
              :triggerResultValidation="isInputValid[index]"
              :viewType="viewType"
              :userAnswer="q.answers"
              :question="q.question"
              :type="q.type"
              @isValid="isResultValid($event, index)"
              @input="q.answers = $event"
              @answered="appendAnswer(q, $event)"
            />
          </div>
        </div>
      </div>
    </div>

    <div class="buttons is-centered">
      <b-button v-if="viewType==='answer'" @click="cancel" id="cancelBtn" type="is-danger">
        Cancel
      </b-button>
      <b-button
        v-if="viewType==='answer'"
        :loading="btnLoading"
        @click="submit"
        id="submitBtn"
        type="is-success"
        :disabled="!viewingSelfAnswer"
      >
        Submit
      </b-button>
    </div>
  </div>
</template>

<script>
  import DynamicInput from "./DynamicInput";
  import SingleInput from "./SingleInput";

  import api from "@/Api";

  /**
   * This component handles getting user input for
   * creating possible outcomes for an activity

   * It is possible to pass v-model to this component
   * to retrieve the list of outcomes
   */
  export default {
    name: "PossibleResult",
    components: {
      DynamicInput,
      SingleInput
    },
    props: ["value", "userId", "activityId", "viewType", "triggerResultValidation", "userViewingAnswer", "viewingSelfAnswer"],
    data() {
      return {
        isMultiChoiceCombo: false,
        isNumberDecimal: false,
        option: "Text",
        question: "",
        questions: [],
        measurementUnit: "",
        isErrorVisible: false,
        isInputValid: [],
        answers: [],
          answersFromBackend: [],
        isQuestionValid: true,
        btnLoading: false,
      };
    },

    watch: {
      questions: function() {
        this.$emit("qCheck", !this.hasDuplicateQuestions());
        this.$emit("input", this.questions);
        this.$emit("checkValid", this.isInputValid);
      }
    },

    async mounted() {
      await this.preloadData();
      if (this.userViewingAnswer) await this.preloadAnswers();
      this.$emit("loading", false);
    },

    computed: {
      message() {
        if (this.isErrorVisible) {
          return "Field is required";
        }
        return "Input the expected result and press the [+] button";
      },
    },

    methods: {
      async submit() {
        this.btnLoading = true;
        try {
          const payload = this.answers.filter(a => "answer" in a)
          await api.putActivityOutcomes(this.userId, this.activityId, {answers: payload});
          await this.$router.push({
              name: "ViewActivity",
              params: {
                  activityId: this.activityId
              }
          });
        } catch (err) {
          this.$buefy.toast.open({
            duration: 2000,
            message: "Unable to save your answers",
            type: "is-danger",
          });
        } finally {
          this.btnLoading = false;
        }
      },

      cancel: function() {
        this.$router.go(-1);
      },

      isResultValid(value, index) {
        this.$set(this.isInputValid, index, value);
      },

      async preloadData() {
        if (this.activityId) {
          let response = await api.getActivitySpecificationById(this.activityId);
          this.questions = response.data;
          this.questions.sort((i, j) => i.id - j.id);
          this.questions.forEach(x => {
            this.answers.push({id: x.id})
            x.userAnswer = null;
          })
          for (let i = 0; i < this.questions.length; i++) {
            this.isInputValid[i] = true;
          }
        }
      },

      async preloadAnswers() {
        if (this.activityId) {
          let response = await api.getUserOutcomeAnswers(this.userId, this.activityId);
          const answersFromBackend = response.data.answers;
          answersFromBackend.forEach(ans => {
            const questionId = ans.id;
            const idxOfAnswerWithSameId = this.questions.map(a => a.id).indexOf(questionId);
            // This prepopulates the input boxes with existing answers
            this.questions[idxOfAnswerWithSameId].answers = [{answer: ans.answer}];
            // We need to set the answers again so that it is sent again to the backend
            this.answers[idxOfAnswerWithSameId].answer = ans.answer;
          })
        }
      },

      hasDuplicateQuestions() {
        // Find all the questions, remove whitespaces and make them lowercase
        let qs = this.questions.map(q =>
          q.question.replace(/ /g, "").toLowerCase()
        );
        // Create an object to count the number of similar questions
        // If there are questions with count more than 1 ==> duplicate found
        // O(n) solution, baby
        let qsObj = {};
        qs.forEach(q => {
          if (Object.keys(qsObj).includes(q)) {
            qsObj[q] += 1;
          } else {
            qsObj[q] = 1;
          }
        });
        return Object.values(qsObj).filter(q => q > 1).length > 0;
      },

      getOptionName() {
        switch (this.option) {
          case "Number":
            return this.isNumberDecimal ? "float" : "integer";
          case "Multi-Choice":
            return this.isMultiChoiceCombo
              ? "multichoice_combination"
              : "multichoice_single";
          default:
            return this.option.toLowerCase();
        }
      },

      addQuestion() {
        if (!this.question.length) {
          this.isErrorVisible = true;
          return;
        }

        let singleQuestion = {
          type: this.getOptionName(),
          question: this.question,
          answers: [{ answer: "", answered: false }],
          unit: this.measurementUnit
        };
        this.questions.push(singleQuestion);
        this.isInputValid.push(true);
        this.question = "";
        this.measurementUnit = "";
      },

      isSingleInput(type) {
        return ["text", "integer", "float", "date", "time", "checkbox"].includes(
          type
        );
      },

      removeQuestion(index) {
        this.isInputValid.splice(index, 1);
        this.questions.splice(index, 1);
      },

      appendAnswer(question, answer) {
        const qId = question.id
        const selectedAnswer = this.answers.find(a => a.id === qId)
        selectedAnswer.answer = answer
      }
    }
  };
</script>

<style scoped>
  #input-box {
    margin-bottom: -1.5rem;
  }
  .delete-btn {
    display: block;
    margin: auto;
  }
  .spacer {
    margin-bottom: 1rem;
  }

  #units {
    width: 50%;
  }

  .is-expanded {
    width: 90% !important;
  }

  .number-switch {
    margin-top: 0.3rem;
    margin-left: 1rem;
  }

  #submitBtn {
    margin-top: 1rem;
  }

  #cancelBtn {
    margin-top: 1rem;
  }
</style>
