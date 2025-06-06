

package com.example;

/**
 * Represents the inspire_om_lines table with two fields: processed and spir_project_id.
 */
public class InspireOmLines {
  private String processed;
  private Integer spirProjectId;

  /**
   * Returns the processed value.
   * @return the processed value
   */
  public String getProcessed() {
    return processed;
  }

  /**
   * Sets the processed value.
   * @param processed the processed value to set
   * @throws NullPointerException if processed is null
   * @throws IllegalArgumentException if processed is empty
   */
  public void setProcessed(String processed) {
    if (processed == null) {
      throw new NullPointerException("Processed cannot be null");
    }
    if (processed.isEmpty()) {
      throw new IllegalArgumentException("Processed cannot be empty");
    }
    this.processed = processed;
  }

  /**
   * Returns the spir_project_id value.
   * @return the spir_project_id value
   */
  public Integer getSpirProjectId() {
    return spirProjectId;
  }

  /**
   * Sets the spir_project_id value.
   * @param spirProjectId the spir_project_id value to set
   * @throws NullPointerException if spirProjectId is null
   */
  public void setSpirProjectId(Integer spirProjectId) {
    if (spirProjectId == null) {
      throw new NullPointerException("SpirProjectId cannot be null");
    }
    this.spirProjectId = spirProjectId;
  }
}